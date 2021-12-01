package com.lado.travago.tripbook.ui.recycler_adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ItemTownsOverviewBinding
import com.lado.travago.tripbook.model.booking.TownsOverview

class BusOverviewAdapter(
    private val clickListener: BusOverviewClickListener,
    private val res: Resources,
) : ListAdapter<TownsOverview, BusOverviewViewHolder>(
    BusOverviewDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusOverviewViewHolder =
        BusOverviewViewHolder.from(parent, res)

    override fun onBindViewHolder(holder: BusOverviewViewHolder, position: Int) = holder.bind(
        clickListener, getItem(position)
    )
}

class BusOverviewViewHolder private constructor(
    val binding: ItemTownsOverviewBinding,
    val res: Resources,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: BusOverviewClickListener, townsOverview: TownsOverview) {

        binding.clickListener = clickListener
        binding.busOverview = townsOverview

        //Information
        binding.textTown.text = townsOverview.townName
//        binding.textRegion.text = busOverview.regionName


        if (townsOverview.fromLocality != null) {
            //In this case, we on the trip
            binding.textBusesCount.visibility = View.GONE
            //We show the qr scan button
            binding.btnEnter.icon =
                ResourcesCompat.getDrawable(res, R.drawable.baseline_qr_code_scanner_24, null)
        } else {
            //In this case, we are on the localities
            binding.textBusesCount.text = townsOverview.busCounts.toString()
            //We show the continue button
            binding.btnEnter.icon =
                ResourcesCompat.getDrawable(res, R.drawable.baseline_arrow_forward_24, null)
        }

        binding.textBookersCount.text = townsOverview.bookersCount.toString()
        binding.textScansCount.text = townsOverview.scansCount.toString()

    }

    companion object {
        fun from(parent: ViewGroup, res: Resources): BusOverviewViewHolder {
            val binding = ItemTownsOverviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return BusOverviewViewHolder(binding, res)
        }
    }
}

class BusOverviewClickListener(val clickListener: (townName: String) -> Unit) {
    fun onClick(townsOverview: TownsOverview) = clickListener(townsOverview.townName)
}

class BusOverviewDiffUtils : DiffUtil.ItemCallback<TownsOverview>() {
    override fun areItemsTheSame(oldItem: TownsOverview, newItem: TownsOverview) =
        oldItem.townName == newItem.townName

    override fun areContentsTheSame(oldItem: TownsOverview, newItem: TownsOverview) =
        oldItem == newItem
}