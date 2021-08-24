package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemTownConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TownsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.util.*

/**
 * This adapter adapts  for the town configuration page
 *  It has as input towns Document and that agencies exception document
 *  towns KEY ="townDoc"
 *  @param exemptedTownsList is that agency document which contains exceptions about towns which the agency can offer or not
 *  exceptions doc KEY = "exceptionDoc"
 */
@ExperimentalCoroutinesApi
class TownConfigAdapter (val clickListener: TownClickListener, private val exemptedTownsList: List<String>) : ListAdapter< DocumentSnapshot, TownConfigViewHolder>(
    TownConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TownConfigViewHolder =
        TownConfigViewHolder.from(parent, exemptedTownsList)

    override fun onBindViewHolder(holder: TownConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))

}

@ExperimentalCoroutinesApi
class TownConfigViewHolder private constructor(val binding: ItemTownConfigBinding, private val exemptedTownsList: List<String>) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * @param townDoc is a document containing each of the town in firestore
     */
    fun bind(clickListener: TownClickListener, townDoc: DocumentSnapshot) {
        binding.textTown.text = townDoc["name"].toString()
        binding.textRegion.text = townDoc["region"].toString()
        binding.clickListener = clickListener
        binding.townDoc = townDoc
        //If the current town is not found the exception townList, it is checked by default else it is not checked
        binding.switchActivate.isChecked = !exemptedTownsList .contains(townDoc.id)
    }


    companion object {
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the town item view holder with this binding.
         */
        fun from(parent: ViewGroup, exemptedTownsList: List<String>): TownConfigViewHolder {
            val binding = ItemTownConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TownConfigViewHolder(binding,exemptedTownsList)
        }
    }
}


/**
 * When ever a button, or check is tapped on the town recucler, we get the id of the town clicked
 */
@ExperimentalCoroutinesApi
class TownClickListener(val clickListener: (townID: String, townButtonTag: TownsConfigViewModel.TownButtonTags) -> Unit){
    /**
     * @param buttonID is the latout id of the button which has been clicked
     */
    fun onClick(townButtonTag: TownsConfigViewModel.TownButtonTags, townDoc: DocumentSnapshot) = clickListener("${townDoc.id}+${townDoc["name"]}", townButtonTag)
}

class TownConfigDiffCallbacks : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem == newItem
}

