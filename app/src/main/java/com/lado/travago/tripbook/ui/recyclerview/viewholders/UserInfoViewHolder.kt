package com.lado.travago.tripbook.ui.recyclerview.viewholders


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lado.travago.tripbook.databinding.ItemUserInfoBinding
import com.lado.travago.tripbook.model.users.Scanner
import com.lado.travago.tripbook.model.users.User

/**
 * The ViewHolder to host the [User.UserBasicInfo]
 */
class UserInfoViewHolder private constructor(val binding: ItemUserInfoBinding) :
    ViewHolder(binding.root) {

    /**
     * A helper function  which will help the adapter bind the right data from the [User.UserBasicInfo]
     * properties to the different views from the [ItemUserInfoBinding].
     * @param userInfo is a [Scanner.ScannerBasicInfo] object from the list which will be provided
     */
    fun bind(userInfo: User.UserBasicInfo){
        binding.userInfo = userInfo
        //Let binding do all the bindings processes
        binding.executePendingBindings()
    }

    companion object{
        /**
         * Used to create this view holder.
         * @param parent is the layout which will host the the view holder
         * @return the scanner item view holder with this binding.
         */
        fun from(parent: ViewGroup): UserInfoViewHolder {
            val binding = ItemUserInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return UserInfoViewHolder(binding)
        }
    }
}

