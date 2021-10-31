package com.lado.travago.tripbook.ui.recycler_adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ItemTimeIntervalBinding
import com.lado.travago.tripbook.model.admin.TimeModel


class TripDepartureTimeConfigAdapter(
    private val clickListener: TimeIntervalClickListener,
    private val timeFormat: TimeModel.TimeFormat,
    private val resources: Resources
) : ListAdapter<DocumentSnapshot, TimeIntervalViewHolder>(
    TimeIntervalDiffUtils()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TimeIntervalViewHolder.from(parent, timeFormat, resources)

    override fun onBindViewHolder(holder: TimeIntervalViewHolder, position: Int) = holder.bind(
        clickListener, getItem(position)
    )
}


class TimeIntervalViewHolder private constructor(
    val binding: ItemTimeIntervalBinding,
    private val timeFormat: TimeModel.TimeFormat,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: TimeIntervalClickListener, doc: DocumentSnapshot) {
        binding.clickListener = clickListener
        binding.document = doc
        binding.textIntervalName.text = doc.getString("intervalName")!!

        //We format the time in a 24h format and display it to the screen
        val fromTime =
            TimeModel.from24Format(
                doc.getLong("fromHour")!!.toInt(),
                doc.getLong("fromMinutes")!!.toInt(),
                null
            ).formattedTime(timeFormat)

        val toTime =
            TimeModel.from24Format(
                doc.getLong("toHour")!!.toInt(),
                doc.getLong("toMinutes")!!.toInt(),
                null
            ).formattedTime(timeFormat)
        val timeInterval ="$fromTime ${resources.getString(R.string.text_label_to)} $toTime"
        binding.textTimeInterval.text = timeInterval
        binding.textDepartureTime.text =
            TimeModel.from24Format(
                doc.getLong("departureHour")!!.toInt(),
                doc.getLong("departureMinutes")!!.toInt(),
                null
            ).formattedTime(timeFormat)
    }

    companion object {
        fun from(parent: ViewGroup, timeFormat: TimeModel.TimeFormat, resources: Resources): TimeIntervalViewHolder {
            val binding = ItemTimeIntervalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TimeIntervalViewHolder(binding, timeFormat, resources)
        }
    }
}

class TimeIntervalClickListener(val clickListener: (intervalID: String) -> Unit) {
    fun onClick(intervalID: String) = clickListener(intervalID)
}

class TimeIntervalDiffUtils : DiffUtil.ItemCallback<DocumentSnapshot>() {
    override fun areItemsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DocumentSnapshot, newItem: DocumentSnapshot) =
        oldItem == newItem
}