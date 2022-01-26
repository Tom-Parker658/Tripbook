package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemTripSearchResultsBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.enums.PlaceHolder
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.imageFromUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripSearchResultsAdapter(
    private val clickListener: TripSearchResultsClickListener,
) : ListAdapter<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>, TripSearchResultsViewHolder>(
    TripSearchResultDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripSearchResultsViewHolder.from(parent)

    override fun onBindViewHolder(holder: TripSearchResultsViewHolder, position: Int) =
        holder.bind(
            clickListener,
            getItem(position).first,
            getItem(position).second,
            getItem(position).third
        )
}

class TripSearchResultsViewHolder(
    private val binding: ItemTripSearchResultsBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        clickListener: TripSearchResultsClickListener,
        agencyDoc: DocumentSnapshot,
        tripDoc: DocumentSnapshot,
        departureTime: TimeModel,
    ) {
        // Data def
        binding.agencyDoc = agencyDoc
        binding.clickListener = clickListener
        binding.tripTime = departureTime
        binding.tripDoc = tripDoc

        // pending bindings
        binding.agencyLogo.imageFromUrl(
            agencyDoc.getString("logoUrl")!!, PlaceHolder.AGENCY, null
        )
        binding.textAgencyName.let {
            it.text = agencyDoc.getString("agencyName")
            //We set the verification drawable if the agency is verified
            if (agencyDoc.getBoolean("isVerified")!!) {
                binding.verifiedBitmap.visibility = View.VISIBLE
            } else binding.verifiedBitmap.visibility = View.GONE
        }
        binding.textIntervalDeparture.text =
            departureTime.formattedTime(TimeModel.TimeFormat.FORMAT_24H)
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

class TripSearchResultsClickListener(val clickListener: (agencyId: String, agencyName: String, tripDoc: DocumentSnapshot, tripTime: TimeModel, vip: Boolean) -> Unit) {
    fun onClick(
        agencyDoc: DocumentSnapshot,
        tripDoc: DocumentSnapshot,
        tripTime: TimeModel,
        isVip: Boolean,
    ) = clickListener(agencyDoc.id, agencyDoc.getString("agencyName")!!, tripDoc, tripTime, isVip)
}

class TripSearchResultDiffUtils :
    DiffUtil.ItemCallback<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>() {
    override fun areItemsTheSame(
        oldItem: Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>,
        newItem: Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>,
    ) = oldItem.first.id == oldItem.first.id

    override fun areContentsTheSame(
        oldItem: Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>,
        newItem: Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>,
    ) = oldItem == newItem

}