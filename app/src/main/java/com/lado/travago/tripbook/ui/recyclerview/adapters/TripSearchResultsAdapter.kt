package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.lado.travago.tripbook.databinding.ItemTripSearchResultsBinding
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchResultsAdapter(
//    private val agencyDocList: List<DocumentSnapshot>,
    private val tripDistance: Long,
    private val clickListener: TripSearchResultsClickListener
) : ListAdapter<DocumentSnapshot, TripSearchResultsViewHolder>(
    TripSearchResultDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripSearchResultsViewHolder.from(parent, tripDistance)

    override fun onBindViewHolder(holder: TripSearchResultsViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))
}

class TripSearchResultsViewHolder(
    private val binding: ItemTripSearchResultsBinding,
    private val distance: Long
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: TripSearchResultsClickListener, agencyDoc: DocumentSnapshot) {
        // Data def
        binding.agencyDoc = agencyDoc
        binding.clickListener = clickListener
        // pending bindings
        binding.agencyLogo.loadImageFromUrl(agencyDoc.getString("logoUrl")!!)
        binding.textAgencyName.let {
            //We set the verification drawable if the agency is verified
            val checkDrawable = binding.verifiedBitmap.drawable
            if (agencyDoc.getBoolean("isVerified")!!) it.setCompoundDrawablesRelative(
                null,
                null,
                checkDrawable,
                null
            )
            else it.setCompoundDrawablesRelative(null, null, null, null)
        }
        binding.ratingBar.progress = agencyDoc.getLong("reputation")!!.toInt()
        binding.textAgencyMotto.text = agencyDoc.getString("motto")
        binding.textTripPrice.text = "${agencyDoc.getLong("pricePerKm")!! * distance}"
        binding.textVipPrice.text = "${agencyDoc.getLong("vipPricePerKm")!! * distance}"
    }

    companion object {
        fun from(parent: ViewGroup, tripDistance: Long): TripSearchResultsViewHolder {
            val binding = ItemTripSearchResultsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripSearchResultsViewHolder(
                binding, tripDistance
            )
        }
    }
}

class TripSearchResultsClickListener(val clickListener: (agencyId: String) -> Unit) {
    fun onClick(agencyDoc: DocumentSnapshot) = clickListener(agencyDoc.id)
}

class TripSearchResultDiffUtils : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem == newItem
}