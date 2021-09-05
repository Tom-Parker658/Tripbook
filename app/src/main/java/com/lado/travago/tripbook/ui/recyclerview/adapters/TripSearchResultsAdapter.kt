package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ItemTripSearchResultsBinding
import com.lado.travago.tripbook.utils.loadImageFromUrl
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
        destinationDoc: DocumentSnapshot
    ) {
        // Data def
        binding.agencyDoc = agencyDoc
        binding.clickListener = clickListener
        // pending bindings
        binding.agencyLogo.loadLogoFromUrl(
            agencyDoc.getString("logoUrl")!!
        )
        binding.textAgencyName.let {
            it.text = agencyDoc.getString("name")
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
        binding.ratingBar.progress = agencyDoc.getDouble("reputation")!!.toInt()
        binding.textAgencyMotto.text = agencyDoc.getString("motto")

        binding.btnTripPrice.text = "${destinationDoc.getLong("pricePerKm")!!}"

        if (destinationDoc.getBoolean("isVip")!!) {
            binding.textVipPrice.text = "${agencyDoc.getLong("vipPricePerKm")!!}"
            binding.textVipPrice.visibility = View.GONE
        }else{
            binding.textVipPrice.visibility = View.VISIBLE
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