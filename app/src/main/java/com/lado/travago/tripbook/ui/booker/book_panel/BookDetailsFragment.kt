package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookDetailsBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.MyBooksViewModel
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class BookDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBookDetailsBinding
    private lateinit var viewModel: MyBooksViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_book_details,
            container,
            false
        )
        getBookDocArgs()
        inflateBook()
        return binding.root
    }

    private fun getBookDocArgs() {
        viewModel = ViewModelProvider(requireActivity())[MyBooksViewModel::class.java]
        val failedString = BookDetailsFragmentArgs.fromBundle(requireArguments()).failedString
        viewModel.getSelectedBookFromFailedString(failedString)
    }

    private fun inflateBook() {
        viewModel.selectedBookDoc.also { doc ->
            //Book Header
            binding.textBookPrice.text = Utils.formatFCFAPrice(doc.getLong("price")!!)
            binding.imageBookVip.let {
                if (doc.getBoolean("isVip")!!) it.visibility = View.VISIBLE
                else it.visibility = View.GONE
            }
            binding.bookStatusImage.let {
                when {
                    //Pending
                    !doc.getBoolean("isExpired")!! -> {
                        it.setImageDrawable(resources.getDrawable(R.drawable.outline_schedule_24))
                        it.setColorFilter(resources.getColor(R.color.colorPositiveButton))
                    }
                    //Missed the trip
                    doc.getBoolean("isExpired")!! && !doc.getBoolean("isScanned")!! -> {
                        it.setImageDrawable(resources.getDrawable(R.drawable.round_cancel_24))
                        it.setBackgroundColor(resources.getColor(R.color.colorNegativeButton))
                    }
                    doc.getBoolean("isScanned")!! -> {
                        it.setImageDrawable(resources.getDrawable(R.drawable.baseline_verified_24))
                        it.setBackgroundColor(resources.getColor(R.color.colorPositiveButton))
                    }
                }
            }

            //Book Body
            binding.textBookOwner.text = doc.getString("bookerName")
            binding.textBookPhone.text = FirebaseAuthRepo().currentUser!!.phoneNumber
            binding.textBookAgency.text = doc.getString("agencyName")
            binding.textBookTripDate.text =
                Utils.formatDate(doc.getLong("travelDateMillis")!!, "dd-MM-yyyy")
            binding.textBookTripTime.text =
                TimeModel.fromSeconds(doc.getLong("departureTime")!!.toInt())
                    .formattedTime(TimeModel.TimeFormat.FORMAT_24H)
            binding.textBookLocality.text = doc.getString("localityName")
            binding.textBookDestination.text = doc.getString("destinationName")

            //Booker Footer
            binding.textBookTripbook.text = "TripBook"
            val generatedOnText = "Generated On: ${
                doc.getTimestamp("generatedOn")!!.toDate()
            }"
            binding.textBookTimestamp.text = generatedOnText

            //The grand "frère" i.e the qr-code
            val qrBitmap = Utils.bookQRCodeGenerator(
                qrCodeText = doc.getString("failed")!!,
                requiredHeight = 200,
                requiredWidth = 200
            )
            Glide.with(requireContext())
                .load(qrBitmap)
                .error(R.drawable.baseline_image_error_24)
                .into(binding.imageQrCode)

            binding.imageQrCode.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setBackground(binding.imageQrCode.drawable)
                    .show()
            }

        }

    }

}