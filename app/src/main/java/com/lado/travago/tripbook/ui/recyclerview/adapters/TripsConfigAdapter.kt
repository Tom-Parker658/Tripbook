package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemTripsConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @param optionMapList contains a list of vip Boolean, vip price, normal price, and tripID for all trips
 */

@ExperimentalCoroutinesApi
class TripsConfigAdapter(
    val clickListener: TripsClickListener,
    private val exemptedTripsList: List<String>,
    private val optionMapList: List<Map<String, Any>>,
    private val pricePerKM: Double,
    private val vipPricePerKM: Double
) : ListAdapter<DocumentSnapshot, TripsConfigViewHolder>(
    TripsConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsConfigViewHolder =
        TripsConfigViewHolder.from(
            parent = parent,
            exemptedTownsList = exemptedTripsList,
            optionMapList = optionMapList,
            pricePerKM = pricePerKM,
            vipPricePerKM = vipPricePerKM
        )

    override fun onBindViewHolder(holder: TripsConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))

}

@ExperimentalCoroutinesApi
class TripsConfigViewHolder private constructor(
    val binding: ItemTripsConfigBinding,
    private val exemptedTripsList: List<String>,
    private val optionMapList: List<Map<String, Any>>,
    private val pricePerKM: Double,
    private val vipPricePerKM: Double
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

        //If the current trip is not found the exception tripList for this town, it is checked by default else it is not checked
        binding.switchActivate.isChecked = !exemptedTripsList.contains(tripDoc.id)
        optionMapList.find {
            it["townID"] == tripDoc.id
        }.let {
            if (!it.isNullOrEmpty()) {
                (it["vip"] as Boolean).let { vip ->
                    binding.checkVip.isChecked = vip
                    if (vip) binding.btnPriceVip.text = it["vipPrice"].toString()
                }
                binding.btnPrice.text = it["normalPrice"].toString()
            } else {
                binding.btnPrice.text =
                    "${(tripDoc.getLong("distance")!! * pricePerKM).toInt()} FCFA"
                binding.checkVip.isChecked = true
                binding.btnPriceVip.text =
                    "${(tripDoc.getLong("distance")!! * vipPricePerKM).toInt()} FCFA"
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
            exemptedTownsList: List<String>,
            optionMapList: List<Map<String, Any>>,
            pricePerKM: Double,
            vipPricePerKM: Double
        ): TripsConfigViewHolder {
            val binding = ItemTripsConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripsConfigViewHolder(
                binding = binding,
                exemptedTripsList = exemptedTownsList,
                optionMapList = optionMapList,
                pricePerKM = pricePerKM,
                vipPricePerKM = vipPricePerKM
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

