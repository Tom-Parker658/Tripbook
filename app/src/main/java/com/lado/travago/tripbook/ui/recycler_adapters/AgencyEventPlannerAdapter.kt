package com.lado.travago.tripbook.ui.recycler_adapters

import android.content.res.Resources
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ItemAgencyEventBinding

class AgencyEventPlannerAdapter(
    private val clickListener: AgencyEventPlannerClickListener
) : ListAdapter<DocumentSnapshot, AgencyEventPlannerViewHolder>(
    AgencyEventPlannerDiffUtil()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AgencyEventPlannerViewHolder.from(parent)

    override fun onBindViewHolder(holder: AgencyEventPlannerViewHolder, position: Int) =
        holder.bind(clickListener, getItem(position))
}

class AgencyEventPlannerViewHolder(val binding: ItemAgencyEventBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: AgencyEventPlannerClickListener, eventDoc: DocumentSnapshot) {
        if (!eventDoc.getBoolean("isExpired")!!) {
            binding.clickListener = clickListener
            binding.eventDoc = eventDoc
            binding.textEventType.text = eventDoc.getString("eventType")!!
            binding.textEventReason.text = eventDoc.getString("eventReason")!!
            binding.textEventDate.text = "${eventDoc.getTimestamp("eventDate")!!.toDate()}"
            //Event is on going and it has not yet expired or being cancelled
            if (eventDoc.getTimestamp("eventDate")!! > Timestamp.now()) {
                binding.btnAction.text = "Stop Event"
                binding.imageView7.setImageDrawable(
                    Resources.getSystem().getDrawable(R.drawable.outline_schedule_24)
                )
            }else{//Event can still be canceled
                binding.btnAction.text = "Cancel Event"
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): AgencyEventPlannerViewHolder {
            val binding: ItemAgencyEventBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_agency_event,
                null,
                false
            )
            return AgencyEventPlannerViewHolder(binding)
        }
    }
}

class AgencyEventPlannerClickListener(val clickListener: (eventID: String) -> Unit) {
    fun onClick(eventID: String) = clickListener(eventID)
}

class AgencyEventPlannerDiffUtil : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areContentsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem == newItem
}