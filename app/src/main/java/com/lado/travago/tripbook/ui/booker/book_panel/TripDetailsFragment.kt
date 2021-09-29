package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ListenerRegistration
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripDetailBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.error.ErrorHandler.handleError
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripsDetailsViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripsDetailsViewModel.*
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat

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
        documentsListener
        initNumberPicker()

        return binding.root
    }
    //TODO: Make sure trip documents also hold references to the regions

    private val documentsListener: ListenerRegistration
        get() {
            viewModel.setField(FieldTags.ON_LOADING, true)
            /*1- We get the agency document*/
            return viewModel.firestoreRepo.db.document("OnlineTransportAgency/${viewModel.agencyID}")
                .addSnapshotListener(requireActivity()) { agencyDoc, agencyError ->
                    viewModel.setField(FieldTags.ON_LOADING, true)
                    if (agencyDoc != null) {
                        viewModel.setField(FieldTags.AGENCY_DOC, agencyDoc)

                        /*2- We get the selected trip document from the agency's trips collection */
                        viewModel.firestoreRepo.db.document(
                            "OnlineTransportAgency/${
                                viewModel.agencyID
                            }/Planets_agency/Earth_agency/Continents_agency/Africa_agency/Cameroon_agency/land/Trips_agency/${viewModel.tripID}"
                        ).addSnapshotListener(requireActivity()) { tripDoc, tripError ->
                            if (tripDoc != null) {
                                viewModel.setField(FieldTags.TRIP_DOC, tripDoc)
                                /*3- We fill layout with information*/
                                viewModel.setField(FieldTags.ON_LOADING, false)
                                bindAllViews()
                            } else {
                                viewModel.setField(FieldTags.ON_LOADING, false)
                                viewModel.setField(
                                    FieldTags.TOAST_MESSAGE,
                                    tripError?.handleError { }.toString()
                                )
                            }
                        }
                    } else {
                        viewModel.setField(
                            FieldTags.TOAST_MESSAGE,
                            agencyError?.handleError { }.toString()
                        )
                        viewModel.setField(FieldTags.ON_LOADING, false)
                    }
                }
        }

    private fun initNumberPicker() {
        binding.numberPicker.run {
            value = 1
            minValue = 0
            maxValue = 140
            setOnValueChangedListener { _, selectedNumber, _ ->
                viewModel.setField(FieldTags.NUMBER_OF_BOOKS, selectedNumber)
                bindPrices()
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
        }
    }

    private fun bindAllViews() = binding.apply {
        //First Card in order
        textAgency.text = viewModel.agencyDoc.getString("agencyName")
        ratingReputationBar.rating = viewModel.agencyDoc.getDouble("reputation")!!.toFloat()
        (viewModel.tripDoc["townNames"] as HashMap<String, String>).let {
            textLocality.text = viewModel.destinationTownName
            if (viewModel.destinationTownName != it["town1"]) textDestination.text = it["town1"]
            else it["town2"]
        }
        textDistances.text = "${viewModel.tripDoc.getLong("distance")} Km"
        textTimeTaken.text = Utils.timeTakenCalculator(
            viewModel.tripDoc.getLong("distance")!!.toDouble(),
            60L
        )
        textTripDate.text = Utils.formatDate(viewModel.tripDateInMillis, "EEEE dd MMMM YYYY")
        textTripDepartureTime.text =
            viewModel.tripTime.formattedTime(TimeModel.TimeFormat.FORMAT_24H)

        //Second card in order
        binding.checkIsVip.isChecked = viewModel.isVip
        bindPrices()

    }



    private fun bindPrices() {
        val tripPrice = when (viewModel.isVip) {
            true -> {
                if (viewModel.tripDoc.getBoolean("flagVipPriceFromDistance")!!) {
                    (viewModel.agencyDoc.getDouble("vipPricePerKm")!! * viewModel.tripDoc.getLong("distance")!!).toLong()
                } else {
                    viewModel.tripDoc.getLong("vipPrice")!!
                }
            }
            false -> {
                if (viewModel.tripDoc.getBoolean("flagNormalPriceFromDistance")!!) {
                    (viewModel.agencyDoc.getDouble("normalPricePerKm")!! * viewModel.tripDoc.getLong(
                        "distance"
                    )!!).toLong()
                } else {
                    viewModel.tripDoc.getLong("normalPrice")!!
                }
            }
        }
        binding.apply {
            textSeatsTimesUnitPrice.text = "${viewModel.numberOfBooks} x $tripPrice"
            textTotalPrice.text = Utils.formatFCFAPrice(viewModel.numberOfBooks * tripPrice)
        }

    }


}