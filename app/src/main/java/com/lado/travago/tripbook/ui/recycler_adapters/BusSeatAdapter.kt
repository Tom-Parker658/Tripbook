package com.lado.travago.tripbook.ui.recycler_adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.DocumentSnapshot
//import com.lado.travago.tripbook.databinding.ItemBusSeatBinding
//import com.lado.travago.tripbook.model.admin.BusMatrix
//
///**
// * Draws a bus as a matrix containing seats which can either be taken or not
// * A bus seat is attributed to a cell number or matrix cell iff that cell number is not in any path
// */
//class BusSeatAdapter(
//    val clickListener: BusSeatClickListener,
//    private val currentBookerID: String,
//    private val busType: BusMatrix.BusType
//) : ListAdapter<Pair<DocumentSnapshot?, Int>, BusSeatViewHolder>(
//    BusSeatDiffCallbacks()
//) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
//        BusSeatViewHolder.from(parent, currentBookerID, busType)
//
//    override fun onBindViewHolder(holder: BusSeatViewHolder, position: Int) =
//        holder.bind(clickListener, getItem(position))
//}
//
//class BusSeatViewHolder private constructor(
//    val binding: ItemBusSeatBinding,
//    val bookerID: String,
//    val busType: BusMatrix.BusType
//) : RecyclerView.ViewHolder(binding.root) {
//    fun bind(
//        clickListener: BusSeatClickListener,
//        cellNumberToBusSeatDoc: Pair<DocumentSnapshot?, Int>
//    ) {
//        /*<-----------------------Constructing the bus----------------------->*/
//        val busMatrix = BusMatrix(busType)
//        //1- We make invisible all the path cells from the matrix
//        val allPaths =
//            busMatrix.driverSeatPath + busMatrix.lastDoorPath + busMatrix.corridorPath + busMatrix.firstDoorPath
//        if ((allPaths).contains(cellNumberToBusSeatDoc.second)) {
//            binding.actualSeatNumber = ""
//            binding.card.visibility = View.GONE
//        } else {
//            //If it is not a path cell we can make it a seat cell with a seat number
//            binding.actualSeatNumber =
//                cellNumberToBusSeatDoc.first!!.getLong("seatNumber").toString()
//            //3- We check the bus seat if it is owned by the current booker
//            if (cellNumberToBusSeatDoc.first!!.getString("bookerID") == bookerID)
//                binding.imageYourSeat.visibility = View.VISIBLE
//        }
//        //2- We indicate some info about the different paths but only on the first cell of the path
//        // That is: we  indicate "Driver" on the first path of the driver path and make the rest invisible
//        when (cellNumberToBusSeatDoc.second) {
//            busMatrix.driverSeatPath.first() -> {
//                binding.constraintLayout.visibility = View.GONE
//                binding.textPlaceholder.text = "Driver"
//            }
//            busMatrix.corridorPath.first() -> {
//                binding.constraintLayout.visibility = View.GONE
//                binding.textPlaceholder.text = "Corridor"
//            }
//            busMatrix.firstDoorPath.first() -> {
//                binding.constraintLayout.visibility = View.GONE
//                binding.textPlaceholder.text = "Entrance 1"
//            }
//            busMatrix.lastDoorPath.first() -> {
//                binding.constraintLayout.visibility = View.GONE
//                binding.textPlaceholder.text = "Entrance 2"
//            }
//        }
//
//        //4-We set the
//
//    }
//
//
//    companion object {
//        fun from(
//            parent: ViewGroup,
//            bookerID: String,
//            busType: BusMatrix.BusType
//        ): BusSeatViewHolder {
//            val binding = ItemBusSeatBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//            return BusSeatViewHolder(
//                binding,
//                bookerID,
//                busType
//            )
//        }
//    }
//}
//
//class BusSeatDiffCallbacks : DiffUtil.ItemCallback<Pair<DocumentSnapshot?, Int>>() {
//    override fun areItemsTheSame(
//        oldItem: Pair<DocumentSnapshot?, Int>,
//        newItem: Pair<DocumentSnapshot?, Int>
//    ) = oldItem.second == newItem.second
//
//    override fun areContentsTheSame(
//        oldItem: Pair<DocumentSnapshot?, Int>,
//        newItem: Pair<DocumentSnapshot?, Int>
//    ) = oldItem.second == newItem.second
//
//}
//
//class BusSeatClickListener(val clickListener: (seatNumber: Int?) -> Unit) {
//    /**
//     * @param seatNumber is just the seat number for that seat else an empty string if we are in a cell path
//     */
//    fun onClick(
//        seatNumber: String
//    ) =
//        if (seatNumber.isNotBlank()) clickListener(seatNumber.toInt())
//        else clickListener(null)
//
//}