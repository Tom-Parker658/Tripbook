package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemTripSearchResultsBinding
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.loadLogoFromUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchResultsAdapter(
    private val clickListener: TripSearchResultsClickListener
) : ListAdapter<Pair<DocumentSnapshot, DocumentSnapshot>, TripSearchResultsViewHolder>(
    TripSearchResultDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripSearchResultsViewHolder.from(parent)

    override fun onBindViewHolder(holder: TripSearchResultsViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position).first, getItem(position).second)
}

class TripSearchResultsViewHolder(
    private val binding: ItemTripSearchResultsBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        clickListener: TripSearchResultsClickListener,
        agencyDoc: DocumentSnapshot,
        tripDoc: DocumentSnapshot
    ) {
        // Data def
        binding.agencyDoc = agencyDoc
        binding.clickListener = clickListener
        // pending bindings
        binding.agencyLogo.loadLogoFromUrl(
            agencyDoc.getString("logoUrl")!!
        )
        binding.textAgencyName.let {
            it.text = agencyDoc.getString("agencyName")
            //We set the verification drawable if the agency is verified
            val checkDrawable = binding.verifiedBitmap.drawable
            if (agencyDoc.getBoolean("isVerified")!!) {
                binding.verifiedBitmap.visibility = View.VISIBLE
            } else binding.verifiedBitmap.visibility = View.GONE

        }
        binding.ratingBar.progress = agencyDoc.getDouble("reputation")!!.toInt()
        binding.textAgencyMotto.text = agencyDoc.getString("motto")

        binding.btnNormal.text = Utils.formatFCFAPrice(tripDoc.getLong("normalPrice")!!)

        if (tripDoc.getBoolean("isVip")!!) {
            binding.btnVip.visibility = View.VISIBLE
            binding.btnVip.text = Utils.formatFCFAPrice(tripDoc.getLong("vipPrice")!!)
        } else {
            binding.btnVip.visibility = View.GONE
        }
    }

    companion object {
        fun from(parent: ViewGroup): TripSearchResultsViewHolder {
            val binding = ItemTripSearchResultsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripSearchResultsViewHolder(binding)
        }
    }
}

class TripSearchResultsClickListener(val clickListener: (agencyId: String) -> Unit) {
    fun onClick(agencyDoc: DocumentSnapshot) = clickListener(agencyDoc.id)
}

class TripSearchResultDiffUtils :
    DiffUtil.ItemCallback<Pair<DocumentSnapshot, DocumentSnapshot>>() {
    override fun areItemsTheSame(
        oldItem: Pair<DocumentSnapshot, DocumentSnapshot>,
        newItem: Pair<DocumentSnapshot, DocumentSnapshot>
    ) =
        oldItem.first.id == newItem.first.id

    override fun areContentsTheSame(
        oldItem: Pair<DocumentSnapshot, DocumentSnapshot>,
        newItem: Pair<DocumentSnapshot, DocumentSnapshot>
    ) =
        oldItem.first == newItem.first
}