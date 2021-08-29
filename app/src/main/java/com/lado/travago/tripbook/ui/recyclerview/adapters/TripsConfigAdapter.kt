package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import com.lado.travago.tripbook.databinding.ItemTripsConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TripsConfigAdapter(
    val clickListener: TripsClickListener,
    private val pricePerKM: Double,
    private val vipPricePerKM: Double,
    private val priceChangesMap: List<MutableMap<String, Any?>>
) : ListAdapter<DocumentSnapshot, TripsConfigViewHolder>(
    TripsConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripsConfigViewHolder.from(
            parent = parent,
            pricePerKM = pricePerKM,
            vipPricePerKM = vipPricePerKM,
            priceChangesMap = priceChangesMap
        )

    override fun onBindViewHolder(holder: TripsConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))

}

@ExperimentalCoroutinesApi
class TripsConfigViewHolder private constructor(
    val binding: ItemTripsConfigBinding,
    private val pricePerKM: Double,
    private val vipPricePerKM: Double,
    private val priceChangesMap: List<MutableMap<String, Any?>>
) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * @param tripDoc is a document containing each of the town in firestore
     */
    fun bind(clickListener: TripsClickListener, tripDoc: DocumentSnapshot) {
        binding.textDestinationTown.text = tripDoc["destination"].toString()
        binding.textTripDistance.text = "${tripDoc["distance"].toString()} km"
        binding.clickListener = clickListener
        binding.tripDoc = tripDoc

        val currentTripMap = priceChangesMap.find { it["tripID"] == tripDoc.id }

        currentTripMap.let {
            if (!it.isNullOrEmpty()) {
                val vipPrice = it.getOrElse(
                    "vipPrice", {
                        return@getOrElse (tripDoc.getLong("distance")!! * vipPricePerKM).toLong()
                    }
                ) as Long

                val normalPrice = it.getOrElse(
                    "normalPrice", {
                        return@getOrElse (tripDoc.getLong("distance")!! * pricePerKM).toLong()
                    }
                ) as Long

                val isNotExempted = it.getOrElse("exempted", { return@getOrElse false }) as Boolean
                val isNotVip = it.getOrElse("vip", { return@getOrElse false }) as Boolean

                binding.btnPriceVip.text = "$vipPrice FCFA"
                binding.btnPrice.text = "$normalPrice FCFA"
                binding.switchActivate.isChecked = !isNotExempted
                binding.checkVip.isChecked = !isNotVip
            } else {
                binding.btnPrice.text =
                    "${(tripDoc.getLong("distance")!! * pricePerKM).toInt()} FCFA"
                binding.btnPriceVip.text =
                    "${(tripDoc.getLong("distance")!! * vipPricePerKM).toInt()} FCFA"
                binding.switchActivate.isChecked = true
                binding.checkVip.isChecked = true
            }
        }

        binding.checkVip.isChecked.let {
            if (it) binding.btnPriceVip.visibility = View.VISIBLE
            else binding.btnPriceVip.visibility = View.GONE
        }
    }

    companion object {
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the town item view holder with this binding.
         */
        fun from(
            parent: ViewGroup,
            pricePerKM: Double,
            vipPricePerKM: Double,
            priceChangesMap: List<MutableMap<String, Any?>>
        ): TripsConfigViewHolder {
            val binding = ItemTripsConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripsConfigViewHolder(
                binding = binding,
                pricePerKM = pricePerKM,
                vipPricePerKM = vipPricePerKM,
                priceChangesMap = priceChangesMap
            )
        }
    }
}

/**
 * When ever a button, or check is tapped on the trip recycler, we get the id of the trip clicked
 */
@ExperimentalCoroutinesApi
class TripsClickListener(val clickListener: (tripID: String, townButtonTag: TripsConfigViewModel.TripButtonTags) -> Unit) {
    /**
     * @param  tripButtonTag the layout id of the button which has been clicked
     */
    fun onClick(tripButtonTag: TripsConfigViewModel.TripButtonTags, tripDoc: DocumentSnapshot) =
        clickListener(tripDoc.id, tripButtonTag)
}


class TripsConfigDiffCallbacks : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem == newItem
}
