package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripDetailBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripsDetailsViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripsDetailsViewModel.*
import com.lado.travago.tripbook.utils.Utils
import com.lado.travago.tripbook.utils.loadLogoFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * Seat selection and bus customisation fragment
 */

@ExperimentalCoroutinesApi
class TripDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTripDetailBinding
    private lateinit var viewModel: TripsDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_trip_detail, container, false)
        initViewModelWithArgs()
        observeLiveData()
        viewModel.agencyListener(requireActivity())

        binding.btnProceedToPay.setOnClickListener {
            //TODO: Launch Intent for the payment activity
            viewModel.setField(FieldTags.START_CREATION, true)
            viewModel.setField(FieldTags.START_CREATION, false)
        }

        return binding.root
    }

    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

//    override fun onDestroy() {
//        super.onDestroy()
////        viewModel.agencyListener(requireActivity()).remove()
////        viewModel.tripListener(requireActivity()).remove()
//    }

    //TODO: Make sure trip documents also hold references to the regions
    private fun observeLiveData() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBarDetails.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBarDetails.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.agencyDoc.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.imageLogoAgency.loadLogoFromUrl(it.getString("logoUrl")!!)
            }
            viewModel.tripListener(requireActivity())
        }
        viewModel.tripDoc.observe(viewLifecycleOwner) {
            try {
                bindAllViews()
            } catch (e: Exception) {
            }
        }
        viewModel.numberOfBooks.observe(viewLifecycleOwner) {
            try {
                bindAllViews()
            } catch (e: Exception) {
            }
        }

        viewModel.startCreation.observe(viewLifecycleOwner) {
            if (it)
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.createBookInDB()
                }
        }

        viewModel.tripHasBeenDeleted.observe(viewLifecycleOwner) {
            viewModel.setField(FieldTags.TOAST_MESSAGE, "Sorry! Trip not found!!")
        }

    }

    private fun initNumberPicker() {
        binding.numberPicker.run {
            minValue = 1
            maxValue = 140
            value = viewModel.numberOfBooks.value!!
            setOnValueChangedListener { _, _, _ ->
                viewModel.setField(FieldTags.NUMBER_OF_BOOKS, binding.numberPicker.value)
            }
        }
    }

    private fun initViewModelWithArgs() {
        viewModel = ViewModelProvider(this)[TripsDetailsViewModel::class.java]
        TripDetailsFragmentArgs.fromBundle(requireArguments()).also {
            viewModel.setField(
                FieldTags.ARG_AGENCY_ID,
                it.agencyId
            )
            viewModel.setField(
                FieldTags.ARG_TRIP_ID,
                it.tripId
            )
            viewModel.setField(
                FieldTags.ARG_IS_VIP,
                it.vip
            )
            viewModel.setField(
                FieldTags.ARG_TRIP_TIME,
                (it.tripTimeHour to it.tripTimeMinutes)
            )
            viewModel.setField(
                FieldTags.ARG_TRIP_DATE,
                it.tripDateInMillis
            )
            viewModel.setField(
                FieldTags.LOCALITY_NAME,
                it.localityTownName
            )
        }
    }

    private fun bindAllViews() = binding.also { detailBinding ->
        //First Card in order
        detailBinding.textAgency.text = viewModel.agencyDoc.value!!.getString("agencyName")
        detailBinding.ratingReputationBar.rating =
            viewModel.agencyDoc.value!!.getDouble("reputation")!!.toFloat()
        detailBinding.textLocality.text = viewModel.localityTownName
        (viewModel.tripDoc.value!!["townNames"] as HashMap<String, String>).let {
            detailBinding.textDestination.text =
                if (viewModel.localityTownName != it["town1"]) it["town1"] else it["town2"]
        }
        detailBinding.textDistances.text = "${viewModel.tripDoc.value!!.getLong("distance")} Km"
        detailBinding.textTimeTaken.text = Utils.timeTakenCalculator(
            viewModel.tripDoc.value!!.getLong("distance")!!.toDouble(),
            60L
        )
        detailBinding.textTripDate.text =
            Utils.formatDate(viewModel.tripDateInMillis, "EEEE dd MMMM YYYY")
        detailBinding.textTripDepartureTime.text =
            viewModel.tripTime.formattedTime(TimeModel.TimeFormat.FORMAT_24H)

        //Second card in order
//        binding.checkIsVip.isChecked = viewModel.isVip.value!!
        initNumberPicker()
        bindPrices()
    }

    private fun bindPrices() {
        val tripPrice = when (viewModel.isVip.value!!) {
            true -> {
                if (viewModel.tripDoc.value!!.getBoolean("flagVipPriceFromDistance")!!) {
                    (viewModel.agencyDoc.value!!.getDouble("vipPricePerKm")
                        ?: 12.0 * viewModel.tripDoc.value!!.getLong("distance")!!).toLong()
                } else {
                    viewModel.tripDoc.value!!.getLong("vipPrice")!!
                }//TODO: Remove the price per km
            }
            false -> {
                if (viewModel.tripDoc.value!!.getBoolean("flagNormalPriceFromDistance")!!) {
                    (viewModel.agencyDoc.value!!.getDouble("normalPricePerKm")
                        ?: 11.0 * viewModel.tripDoc.value!!.getLong(
                            "distance"
                        )!!).toLong()
                } else {
                    viewModel.tripDoc.value!!.getLong("normalPrice")!!
                }
            }
        }
        binding.apply {
            val textPrice = "${viewModel.numberOfBooks.value} x $tripPrice"
            textSeatsTimesUnitPrice.text = textPrice
            textTotalPrice.text = Utils.formatFCFAPrice(viewModel.numberOfBooks.value!! * tripPrice)
        }

    }


}