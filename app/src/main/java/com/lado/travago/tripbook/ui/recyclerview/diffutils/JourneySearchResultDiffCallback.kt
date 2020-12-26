package com.lado.travago.tripbook.ui.recyclerview.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.lado.travago.tripbook.model.admin.Journey

class JourneySearchResultDiffCallback: DiffUtil.ItemCallback<Journey.JourneySearchResultInfo>() {
    /**
     * Called to check whether two objects represent the same item.
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the two items represent the same object or false if they are different.
     */
    override fun areItemsTheSame(
        oldItem: Journey.JourneySearchResultInfo,
        newItem: Journey.JourneySearchResultInfo,
    ) = oldItem.agencyName == newItem.agencyName


    /**
     * Called to check whether two items have the same data.
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     */
    override fun areContentsTheSame(
        oldItem: Journey.JourneySearchResultInfo,
        newItem: Journey.JourneySearchResultInfo
    ) = oldItem == newItem

}