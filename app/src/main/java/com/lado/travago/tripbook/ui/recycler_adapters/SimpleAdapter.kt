package com.lado.travago.tripbook.ui.recycler_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lado.travago.tripbook.databinding.ItemSimpleBinding

/**
 * A simple adapter for text selection with an activation button
 */
class SimpleAdapter(
    val clickListener: SimpleClickListener,
    private val selectedItemList: List<String>
) : ListAdapter<HashMap<String, String>, SimpleViewHolder>(
    SimpleDiffCallbacks()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SimpleViewHolder.from(parent, selectedItemList)

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position), position)
    }
}

class SimpleViewHolder private constructor(
    val binding: ItemSimpleBinding,
    private val selectedItemList: List<String>
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: SimpleClickListener, itemMap: HashMap<String, String>, index: Int) {
        binding.textIndex.text = (index + 1).toString()
        binding.textName.text = itemMap["name"]
        //In case it is a trip, this will work else null
        itemMap["distance"]?.let {
            binding.textDistance.text = " $it Km"
        }
        binding.clickListener = clickListener
        binding.itemMap = itemMap

        //Checked when the item is found in the selected list
        binding.checkSelected.isChecked = selectedItemList.contains(itemMap["id"])
    }

    companion object {
        fun from(parent: ViewGroup, selectedItemList: List<String>): SimpleViewHolder {
            val binding = ItemSimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SimpleViewHolder(binding, selectedItemList)
        }
    }

}

class SimpleDiffCallbacks : DiffUtil.ItemCallback<HashMap<String, String>>() {
    override fun areItemsTheSame(
        oldItem: HashMap<String, String>,
        newItem: HashMap<String, String>
    ) = oldItem["id"] == newItem["id"]

    override fun areContentsTheSame(
        oldItem: HashMap<String, String>,
        newItem: HashMap<String, String>
    ) = oldItem == newItem
}

class SimpleClickListener(val clickListener: (itemID: String) -> Unit) {

    fun onClick(itemMap: HashMap<String, String>) =
        clickListener(itemMap["id"] ?: itemMap["townID"] ?: itemMap["tripID"]!!)

}
