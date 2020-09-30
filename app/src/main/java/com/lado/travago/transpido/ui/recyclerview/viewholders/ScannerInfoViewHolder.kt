package com.lado.travago.transpido.ui.recyclerview.viewholders


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lado.travago.transpido.databinding.ItemScannerInfoBinding
import com.lado.travago.transpido.model.admin.Scanner

/**
 * The ViewHolder to host the [Scanner.ScannerBasicInfo]
 */
class ScannerInfoViewHolder private constructor(val binding: ItemScannerInfoBinding) :
    ViewHolder(binding.root) {

    /**
     * A helper function  which will help the adapter bind the right data from the [Scanner.ScannerBasicInfo]
     * properties to the different views from the [ItemScannerInfoBinding].
     * @param scannerInfo is a [Scanner.ScannerBasicInfo] object from the list which will be provided
     */
    fun bind(scannerInfo: Scanner.ScannerBasicInfo){
        binding.scannerInfo = scannerInfo
        //Let binding do all the bindings processes
        binding.executePendingBindings()
    }

    companion object{
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the scanner item view holder with this binding.
         */
        fun from(parent: ViewGroup): ScannerInfoViewHolder {
            val binding = ItemScannerInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ScannerInfoViewHolder(binding)
        }
    }
}

