@file:Suppress("KDocUnresolvedReference")

package com.lado.travago.tripbook.ui.recyclerview.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.lado.travago.tripbook.model.users.User

/**
 * This utility helps reduces lagging when scrolling by exactly knowing what changed and only
 * updating what changed. This is used to replace the less efficient [notifyOnItemChanged]
 */
class UserInfoDiffCallback: DiffUtil.ItemCallback<User.UserBasicInfo>(){
    /**
     * Called to check whether two objects represent the same item.
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the two items represent the same object or false if they are different.
     */
    override fun areItemsTheSame(
        oldItem: User.UserBasicInfo,
        newItem: User.UserBasicInfo
    ) = oldItem.name == newItem.name


    /**
     * Called to check whether two items have the same data.
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     */
    override fun areContentsTheSame(
        oldItem: User.UserBasicInfo,
        newItem: User.UserBasicInfo
    ) = oldItem.equals(newItem)

}