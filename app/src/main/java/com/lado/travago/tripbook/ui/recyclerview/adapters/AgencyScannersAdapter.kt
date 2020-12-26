package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.lado.travago.tripbook.model.admin.Scanner
import com.lado.travago.tripbook.ui.recyclerview.diffutils.ScannerInfoDiffCallback
import com.lado.travago.tripbook.ui.recyclerview.viewholders.ScannerInfoViewHolder

/**
 * A List recycler view adapter for listing an agency scanners
 */
class AgencyScannersAdapter : ListAdapter<Scanner.ScannerBasicInfo, ScannerInfoViewHolder>(
    ScannerInfoDiffCallback()
) {
    /** Creates, inflates and returns [ScannerInfoViewHolder] ViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScannerInfoViewHolder.from(parent)

    /**
     * Binds data gotten from the list to the viewHolder.[getItem] is used to retrieve each object.
     */
    override fun onBindViewHolder(holder: ScannerInfoViewHolder, position: Int) =
        holder.bind(getItem(position))
}