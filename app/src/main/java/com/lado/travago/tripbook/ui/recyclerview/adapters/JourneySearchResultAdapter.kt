package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.lado.travago.tripbook.model.admin.Journey
import com.lado.travago.tripbook.ui.recyclerview.diffutils.JourneySearchResultDiffCallback
import com.lado.travago.tripbook.ui.recyclerview.viewholders.JourneySearchResultViewHolder
import com.lado.travago.tripbook.ui.recyclerview.viewholders.ScannerInfoViewHolder

class JourneySearchResultAdapter: ListAdapter<Journey.JourneySearchResultInfo, JourneySearchResultViewHolder>(
    JourneySearchResultDiffCallback()
) {
    /** Creates, inflates and returns [ScannerInfoViewHolder] ViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneySearchResultViewHolder =
        JourneySearchResultViewHolder.from(parent)

    /**
     * Binds data gotten from the list to the viewHolder.[getItem] is used to retrieve each object.
     */
    override fun onBindViewHolder(holder: JourneySearchResultViewHolder, position: Int) =
        holder.bind(getItem(position))

}