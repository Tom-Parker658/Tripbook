package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemScannerBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.ScannerConfigViewModel
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerConfigAdapter(
    val clickListener: ScannerConfigClickListener,
    private val adminIDsList: List<HashMap<String, Any>>,
) : ListAdapter<DocumentSnapshot, ScannerConfigViewHolder>(
    ScannerConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScannerConfigViewHolder.from(parent, adminIDsList)

    override fun onBindViewHolder(holder: ScannerConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))

}

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerConfigViewHolder private constructor(
    val binding: ItemScannerBinding,
    private val adminIDsList: List<HashMap<String, Any>>
) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * @param scannerDoc is a document containing the scanner in firestore
     */
    fun bind(clickListener: ScannerConfigClickListener, scannerDoc: DocumentSnapshot) {
        binding.scannerDoc = scannerDoc
        binding.scannerPhoto.loadImageFromUrl(scannerDoc.getString("photoUrl")!!)
        binding.textNumberScan.text = "Total Scans: ${scannerDoc["scansNumber"]}"
        binding.textScannerName.text = scannerDoc["name"].toString()
        binding.textScannerPhone.text = scannerDoc["phone"].toString()
        binding.checkIsAdmin.isChecked = scannerDoc["isAdmin"] as Boolean
        binding.textAddedOn.text = "Added on: ${
            Utils.formatDate(
                scannerDoc.getTimestamp("addedOn")!!.toDate().time,  
                SimpleDateFormat("dd MMMM yyyy, HH:mm:ss").toPattern()
            )
        }"

        adminIDsList.find{
            it["id"] == scannerDoc.id
        }?.let{
            binding.checkIsAdmin.isChecked = adminIDsList[adminIDsList.indexOf(it)]["isAdmin"] as Boolean
        }

        binding.checkIsAdmin.isChecked.let {
            binding.adminLogo.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding.clickListener = clickListener
    }

    companion object {
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the town item view holder with this binding.
         */
        fun from(
            parent: ViewGroup,
            adminIDsList: List<HashMap<String, Any>>
        ): ScannerConfigViewHolder {
            val binding = ItemScannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ScannerConfigViewHolder(binding, adminIDsList)
        }
    }
}

/**
 * When ever a button, or check is tapped on the trip recycler, we get the id of the trip clicked
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerConfigClickListener(val clickListener: (scannerID: String, scannerButtonTag: ScannerConfigViewModel.ScannerButtonTags) -> Unit) {
    /**
     * @param  scannerButtonTag the layout id of the button which has been clicked
     */
    fun onClick(
        scannerButtonTag: ScannerConfigViewModel.ScannerButtonTags,
        scannerDoc: DocumentSnapshot
    ) =
        clickListener(scannerDoc.id, scannerButtonTag)
}

class ScannerConfigDiffCallbacks : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem == newItem
}
