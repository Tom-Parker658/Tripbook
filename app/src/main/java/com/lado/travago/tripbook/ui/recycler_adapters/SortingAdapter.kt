package com.lado.travago.tripbook.ui.recycler_adapters
//
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.lado.travago.tripbook.utils.AdminUtils
//
///**
// * @param sortingFields is a pair of the firestore fieldName and the sorting param
// */
//class SortingAdapter(
////    val clickListener: SortingClickListener,
//) : ListAdapter<AdminUtils.SortObject, SortingViewHolder>(
//    SortingDiffCallbacks()
//) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortingViewHolder {
//        TODO("Not yet implemented")
//    }
//
//    override fun onBindViewHolder(holder: SortingViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }
//
//}
//
//class SortingViewHolder : RecyclerView.ViewHolder(val chip: Materi){
//
//}
//
//class SortingDiffCallbacks : DiffUtil.ItemCallback<AdminUtils.SortObject>() {
//    override fun areItemsTheSame(
//        oldItem: AdminUtils.SortObject,
//        newItem: AdminUtils.SortObject
//    ) = oldItem.fieldName == newItem.fieldName
//
//    override fun areContentsTheSame(
//        oldItem: AdminUtils.SortObject,
//        newItem: AdminUtils.SortObject
//    ) = oldItem == newItem
//
//}
