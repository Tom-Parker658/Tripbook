package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.databinding.ItemTripsConfigBinding
import com.lado.travago.tripbook.ui.agency.creation.config_panel.viewmodel.TripsConfigViewModel
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * We use a sort of hash map instead of documents
 */
@ExperimentalCoroutinesApi
class TripsConfigAdapter(
    val clickListener: TripsClickListener,
    private val toDeleteIDList: List<String>,
    private val changesMapList: MutableList<MutableMap<String, Any>>,
    private val currentTownName: String
) : ListAdapter<DocumentSnapshot, TripsConfigViewHolder>(
    TripsConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripsConfigViewHolder.from(parent, toDeleteIDList, changesMapList, currentTownName)

    override fun onBindViewHolder(holder: TripsConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))
}

@ExperimentalCoroutinesApi
class TripsConfigViewHolder private constructor(
    val binding: ItemTripsConfigBinding,
    private val toDeleteIDList: List<String>,
    private val changesMapList: MutableList<MutableMap<String, Any>>,
    private val currentTownName: String
) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * @param tripDoc is sort of a document but is a simple hashMap
     */
    fun bind(clickListener: TripsClickListener, tripDoc: DocumentSnapshot) {
        binding.checkSelectTrip.isChecked = toDeleteIDList.contains(tripDoc.id)
        binding.changesMap = tripDoc
        binding.clickListener = clickListener

        // We first check if this document has been modified by the scanner locally so that we load the
        //locally modified! else the server document
        val find = changesMapList.withIndex().find { tripDoc.id == it.value["id"] }
        if (find != null) {
            find.let { map ->
                //We want to get the other town name different from the current town name
                (map.value["townNames"] as Map<String, String>).also {
                    if (it["town1"] == currentTownName) binding.textDestinationTown.text =
                        it["town2"]
                    else binding.textDestinationTown.text = it["town1"]
                }
//                Bus Types//TODO: Add later
                /*(map.value["busTypes"] as Map<String, Boolean>).also {
                    if (it.values.toList() == listOf(false, false, false))
                        (binding.btnBuses as MaterialButton).setBackgroundColor(
                            Resources.getSystem().getColor(R.color.colorNegativeButton)
                        )
                }*/
                binding.textTripDistance.text = Utils.formatDistance(map.value["distance"] as Long)
                binding.chipVip.isChecked = map.value["isVip"] as Boolean
                binding.btnPriceVip.text = Utils.formatFCFAPrice(map.value["vipPrice"] as Long)
                binding.btnNormalPrice.text = Utils.formatFCFAPrice(map.value["normalPrice"] as Long)
            }
        } else {
            (tripDoc["townNames"] as Map<String, String>).also {
                if (it["town1"] == currentTownName) binding.textDestinationTown.text = it["town2"]
                else binding.textDestinationTown.text = it["town1"]
            }
            /**
             * If there is not bus setup, we color the bus type button red else grey
             */
//            Add later
            /*(tripDoc["busTypes"] as Map<String, Boolean>).also {
                if (it.values.toList() == listOf(false, false, false))
                    (binding.btnBuses as MaterialButton).setBackgroundColor(
                        Resources.getSystem().getColor(R.color.colorNegativeButton)
                    )
            }*/
            binding.chipVip.isChecked = tripDoc.getBoolean("isVip")!!
            binding.textTripDistance.text = Utils.formatDistance(tripDoc.getLong("distance")!!)
            binding.chipVip.isChecked = tripDoc.getBoolean("isVip")!!
            binding.btnPriceVip.text = Utils.formatFCFAPrice(tripDoc.getLong("vipPrice")!!)
            binding.btnNormalPrice.text = Utils.formatFCFAPrice(tripDoc.getLong("normalPrice")!!)

            binding.chipVip.isChecked.let {
                if (it) binding.btnPriceVip.visibility = View.VISIBLE
                else binding.btnPriceVip.visibility = View.GONE
            }
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
            toDeleteIDList: List<String>,
            changesMapList: MutableList<MutableMap<String, Any>>,
            currentTownName: String
        ): TripsConfigViewHolder {
            val binding = ItemTripsConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripsConfigViewHolder(
                binding,
                toDeleteIDList,
                changesMapList,
                currentTownName
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
    fun onClick(
        tripButtonTag: TripsConfigViewModel.TripButtonTags,
        tripDoc: DocumentSnapshot
    ) =
        clickListener(tripDoc.id, tripButtonTag)
}

class TripsConfigDiffCallbacks : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem["id"] == newItem["id"]

    override fun areContentsTheSame(
        oldItem: DocumentSnapshot,
        newItem: DocumentSnapshot
    ) = oldItem == newItem
}
/*
*
/**
 * We use a sort of hash map instead of documents
 */
@ExperimentalCoroutinesApi
class TripsConfigAdapter(
    val clickListener: TripsClickListener,
    private val toDeleteIDList: List<String>
) : ListAdapter<Map<String, Any>, TripsConfigViewHolder>(
    TripsConfigDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TripsConfigViewHolder.from(parent, toDeleteIDList)

    override fun onBindViewHolder(holder: TripsConfigViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))

}

@ExperimentalCoroutinesApi
class TripsConfigViewHolder private constructor(
    val binding: ItemTripsConfigBinding,
    private val toDeleteIDList: List<String>,
) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * @param currentTripMap is sort of a document but is a simple hashMap
     */
    fun bind(clickListener: TripsClickListener, currentTripMap: Map<String, Any>) {
        binding.checkSelectTrip.isChecked = toDeleteIDList.contains(currentTripMap["id"])
        //Data
        binding.changesMap = currentTripMap
        binding.clickListener = clickListener
        binding.textDestinationTown.text = currentTripMap["destination"].toString()
        binding.textTripDistance.text = "${currentTripMap["distance"].toString()} km"
        binding.checkVip.isChecked = currentTripMap["isVip"] as Boolean
        binding.btnPriceVip.text = "${currentTripMap["vipPrice"]} FCFA"
        binding.btnNormalPrice.text = "${currentTripMap["normalPrice"]} FCFA"
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
            toDeleteIDList: List<String>
        ): TripsConfigViewHolder {
            val binding = ItemTripsConfigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TripsConfigViewHolder(binding, toDeleteIDList)
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
    fun onClick(
        tripButtonTag: TripsConfigViewModel.TripButtonTags,
        changesMap: Map<String, Any>
    ) =
        clickListener(changesMap["id"].toString(), tripButtonTag)
}

class TripsConfigDiffCallbacks : DiffUtil.ItemCallback<Map<String, Any>>() {
    override fun areItemsTheSame(
        oldItem: Map<String, Any>,
        newItem: Map<String, Any>
    ) = oldItem["id"] == newItem["id"]

    override fun areContentsTheSame(
        oldItem: Map<String, Any>,
        newItem: Map<String, Any>
    ) = oldItem == newItem
}
*/