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
import com.lado.travago.tripbook.databinding.ItemBusOverviewBinding
import com.lado.travago.tripbook.model.booking.BusOverview

class BusOverviewAdapter(
    private val clickListener: BusOverviewClickListener,
    private val res: Resources
) : ListAdapter<BusOverview, BusOverviewViewHolder>(
    BusOverviewDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusOverviewViewHolder =
        BusOverviewViewHolder.from(parent, res)

    override fun onBindViewHolder(holder: BusOverviewViewHolder, position: Int) = holder.bind(
        clickListener, getItem(position)
    )
}

class BusOverviewViewHolder private constructor(
    val binding: ItemBusOverviewBinding,
    val res: Resources
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: BusOverviewClickListener, busOverview: BusOverview) {
        binding.clickListener = clickListener
        binding.busOverview = busOverview

        //Information
        binding.textTown.text = busOverview.townName
        binding.textRegion.text = busOverview.regionName


        if (busOverview.busCounts == 0) {
            //In this case, we on the trip
            binding.textBusesCount.visibility = View.GONE
            //We show the qr scan button
            binding.btnEnter.icon = ResourcesCompat.getDrawable(res, R.drawable.baseline_qr_code_scanner_24, null)
        } else {
            //In this case, we are on the localities
            binding.textBusesCount.text = busOverview.busCounts.toString()
            //We show the continue button
            binding.btnEnter.icon = ResourcesCompat.getDrawable(res, R.drawable.baseline_arrow_forward_24, null)
        }

        binding.textBookersCount.text = busOverview.bookersCount.toString()
        binding.textScansCount.text = busOverview.scansCount.toString()
    }

    companion object {
        fun from(parent: ViewGroup, res: Resources): BusOverviewViewHolder {
            val binding = ItemBusOverviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return BusOverviewViewHolder(binding, res)
        }
    }
}

class BusOverviewClickListener(val clickListener: (townName: String) -> Unit) {
    fun onClick(busOverview: BusOverview) = clickListener(busOverview.townName)
}

class BusOverviewDiffUtils : DiffUtil.ItemCallback<BusOverview>() {
    override fun areItemsTheSame(oldItem: BusOverview, newItem: BusOverview) =
        oldItem.townName == newItem.townName

    override fun areContentsTheSame(oldItem: BusOverview, newItem: BusOverview) =
        oldItem == newItem
}