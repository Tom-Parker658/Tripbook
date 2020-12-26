package com.lado.travago.tripbook.ui.recyclerview.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lado.travago.tripbook.databinding.ItemJourneySearchResultBinding
import com.lado.travago.tripbook.databinding.ItemScannerInfoBinding
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.model.admin.Scanner


class JourneySearchResultViewHolder private constructor(val binding: ItemJourneySearchResultBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * A helper function  which will help the adapter bind the right data from the [Scanner.ScannerBasicInfo]
     * properties to the different views from the [ItemScannerInfoBinding].
     * @param scannerInfo is a [Scanner.ScannerBasicInfo] object from the list which will be provided
     */
    fun bind(resultInfo: Journey.JourneySearchResultInfo){
        binding.resultInfo = resultInfo
        //Let binding do all the bindings processes
        binding.executePendingBindings()
    }

    companion object{
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the scanner item view holder with this binding.
         */
        fun from(parent: ViewGroup): JourneySearchResultViewHolder {
            val binding = ItemJourneySearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return JourneySearchResultViewHolder(binding)
        }
    }
}