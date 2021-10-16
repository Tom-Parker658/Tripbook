package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lado.travago.tripbook.databinding.ItemSummaryListBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.utils.loadLogoFromUrl


class SummaryItemAdapter(
    private val clickListener: SummaryItemClickListener,
) : ListAdapter<SummaryItem, SummaryItemViewHolder>(
    SummaryItemDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SummaryItemViewHolder.from(parent)

    override fun onBindViewHolder(holder: SummaryItemViewHolder, position: Int) = holder.bind(
        clickListener, getItem(position)
    )
}


class SummaryItemViewHolder private constructor(
    val binding: ItemSummaryListBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: SummaryItemClickListener, item: SummaryItem) {
        binding.clickListener = clickListener
        binding.item = item
        if (item.isMainItem) {
            binding.mainTitle.textSize = 15F
            binding.imageLogo.minimumWidth = 32
            binding.imageLogo.minimumHeight = 32
            binding.divider.visibility = View.VISIBLE

        }
        when (item.state) {
            SummaryItem.SettingsItemState.OK -> {
                binding.imageOk.visibility = View.VISIBLE
            }
            SummaryItem.SettingsItemState.NOT_OK -> {
                binding.imageNotOk.visibility = View.VISIBLE
            }
            SummaryItem.SettingsItemState.PENDING -> {
                binding.imagePending.visibility = View.VISIBLE
            }
        }
        binding.mainTitle.text = item.mainTitle
        binding.subTitle.text = item.subTitle
        item.logoResourceID?.let { binding.imageLogo.setImageResource(it) }
        item.logoUrl?.let {
            binding.imageLogo.loadLogoFromUrl(it)
        }
        item.extraDetails?.let {
            binding.subDetail.text = it
            binding.subDetail.visibility = View.VISIBLE
        }
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): SummaryItemViewHolder {
            val binding = ItemSummaryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SummaryItemViewHolder(binding)
        }
    }
}

class SummaryItemClickListener(val clickListener: (summaryItem: SummaryItem) -> Unit) {
    fun onClick(summaryItem: SummaryItem) = clickListener(summaryItem)
}

class SummaryItemDiffUtils : DiffUtil.ItemCallback<SummaryItem>() {
    override fun areItemsTheSame(oldItem: SummaryItem, newItem: SummaryItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SummaryItem, newItem: SummaryItem) =
        oldItem == newItem
}

