package com.lado.travago.tripbook.ui.recyclerview.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.lado.travago.tripbook.model.users.User
import com.lado.travago.tripbook.ui.recyclerview.diffutils.UserInfoDiffCallback
import com.lado.travago.tripbook.ui.recyclerview.viewholders.UserInfoViewHolder

/**
 * A List recycler view adapter for listing an agency scanners
 */
class UsersAdapter : ListAdapter<User.UserBasicInfo, UserInfoViewHolder>(
    UserInfoDiffCallback()
) {
    /** Creates, inflates and returns [UserInfoViewHolder] ViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserInfoViewHolder.from(parent)

    /**
     * Binds data gotten from the list to the viewHolder.[getItem] is used to retrieve each object.
     */
    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) =
        holder.bind(getItem(position))
}