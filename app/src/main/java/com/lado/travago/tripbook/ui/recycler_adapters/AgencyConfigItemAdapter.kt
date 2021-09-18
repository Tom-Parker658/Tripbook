package com.lado.travago.tripbook.ui.recycler_adapters
//
//import android.view.ViewGroup
//import androidx.recyclerview.widget.ListAdapter
//import com.google.firebase.firestore.DocumentSnapshot
//import com.lado.travago.tripbook.ui.recyclerview.diffutils.ConfigItemResultDiffCallback
//import com.lado.travago.tripbook.ui.recyclerview.viewholders.AgencyConfigViewHolder
//
//
//class AgencyConfigItemAdapter(private val clickListener: ConfigItemClickListener): ListAdapter<DocumentSnapshot, AgencyConfigViewHolder>(
//    ConfigItemResultDiffCallback()
//) {
//    /** Creates, inflates and returns [AgencyConfigViewHolder] ViewHolder */
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyConfigViewHolder =
//        AgencyConfigViewHolder.from(parent)
//
//    /**
//     * Binds data gotten from the list to the viewHolder.[getItem] is used to retrieve each object.
//     */
//    override fun onBindViewHolder(holderAgency: AgencyConfigViewHolder, position: Int) =
//        holderAgency.bind(clickListener, getItem(position)!!)
//
//}
//
///**
// * A class to get the id of the element which has been tapped or is highlighted
// */
//class ConfigItemClickListener(val clickListener: (documentID: String) -> Unit, val textLabel: String){
//    fun onClick(documentSnapshot: DocumentSnapshot) = clickListener(documentSnapshot.id)
//}