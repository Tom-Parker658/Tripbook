package com.lado.travago.tripbook.ui.recyclerview.viewholders
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.DocumentSnapshot
//import com.lado.travago.tripbook.databinding.ItemAgencyConfigBinding
//import com.lado.travago.tripbook.ui.recyclerview.adapters.ConfigItemClickListener
//
//class AgencyConfigViewHolder private constructor(val binding: ItemAgencyConfigBinding)
//    :RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(clickListener: ConfigItemClickListener, item: DocumentSnapshot){
//        binding.doc = item
//        binding.clickListener = clickListener
//        //Let binding do all the bindings processes
//        binding.executePendingBindings()
//    }
//
//    companion object{
//        /**
//         * Used to create this view holder.
//         * @param parent is the layout which will host the the view holder
//         * @return the scanner item view holder with this binding.
//         */
//        fun from(parent: ViewGroup): AgencyConfigViewHolder {
//            val binding = ItemAgencyConfigBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//            return AgencyConfigViewHolder(binding)
//        }
//    }
//}
//
