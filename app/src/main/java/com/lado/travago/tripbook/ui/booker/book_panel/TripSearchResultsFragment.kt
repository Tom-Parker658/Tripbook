package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentTripSearchResultBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.enums.NotificationType
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.TripSearchResultsViewModel.*
import com.lado.travago.tripbook.ui.notification.NotificationFragment
import com.lado.travago.tripbook.ui.notification.NotificationFragmentArgs
import com.lado.travago.tripbook.ui.recycler_adapters.TripSearchResultsAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.TripSearchResultsClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Search Screen result screen fragment
 * planned which will occur before this trip
 * @see AgencyEventPlannerViewModel
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class TripSearchResultsFragment : Fragment() {
    private lateinit var binding: FragmentTripSearchResultBinding
    private lateinit var adapter: TripSearchResultsAdapter
    private lateinit var viewModel: TripSearchResultsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_trip_search_result,
            container,
            false
        )
        initViewModelWithArgs()
        searchListenerLauncher()
        liveDataObserver()

        return binding.root
    }


    private fun searchListenerLauncher() {
        viewModel.tripsListener(requireActivity())
        viewModel.agencyIDList?.let {
            viewModel.agenciesListener(requireActivity(), it)
            viewModel.departureTimeListener(requireActivity(), it)
        }
    }

    private fun removeListeners() {
        viewModel.tripsListener(requireActivity()).remove()
        viewModel.agencyIDList?.let {
            viewModel.agenciesListener(requireActivity(), it).remove()
            viewModel.departureTimeListener(requireActivity(), it).remove()
        }
    }


    private fun initViewModelWithArgs() {
        viewModel = ViewModelProvider(this)[TripSearchResultsViewModel::class.java]
        TripSearchResultsFragmentArgs.fromBundle(requireArguments()).let {
            viewModel.setArguments(
                fromName = it.localityName,
                toName = it.destinationName,
                dateInMillis = it.tripDateInMillis,
                timeInMillis = it.tripTimeInMillis.toLong()
            )
        }
    }

    private fun setRecycler(resultList: MutableList<Triple<DocumentSnapshot, DocumentSnapshot, TimeModel>>) {
        adapter = TripSearchResultsAdapter(
            TripSearchResultsClickListener { agencyID, agencyName, tripDoc, tripTime, isVip ->
                //Navigate to config and stop all snapshot listeners
                val pageTitle = "${
                    if (isVip) "(vip) " else ""
                }${agencyName}"

                removeListeners()
                findNavController().navigate(
                    TripSearchResultsFragmentDirections.actionTripSearchResultsFragmentToTripDetailsFragment(
                        tripDoc.id,
                        agencyID,
                        isVip,
                        viewModel.tripDateInMillis,
                        viewModel.localityName,
                        tripTime.fullTimeInMillis,
                        pageTitle
                    )
                )
            }

        )
        adapter.submitList(resultList)
        binding.recyclerSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResult.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun liveDataObserver() {
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it)
                binding.searchResultProgressBar.visibility = View.VISIBLE
            else
                binding.searchResultProgressBar.visibility = View.GONE

        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onNoSuchResults.observe(viewLifecycleOwner) {
            if (it) {
                val args =
                    NotificationFragmentArgs(
                        NotificationType.EMPTY_RESULTS,
                        "${viewModel.localityName} ${getString(R.string.text_to)} ${viewModel.destinationName}",
                        R.layout.fragment_trip_search_result
                    )
                        .toBundle()
                //To avoid going back to this screen again after the user presses the back button
                findNavController().popBackStack(R.id.tripSearchFragment, false)
                findNavController().navigate(
                    R.id.notificationFragment,
                    args
                )
            }

        }

        viewModel.searchResultsTripleList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setRecycler(it)
            }
        }
        viewModel.allTripsResultsList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.agencyIDList?.let { list ->
                    viewModel.agenciesListener(requireActivity(), list)
                }
            }
        }
        viewModel.allAgenciesResultList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.agencyIDList?.let { list ->
                    viewModel.departureTimeListener(requireActivity(), list)
                }
            }
        }
        viewModel.allDepartureResultList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.prepareForDisplay()
            }
        }

    }
}
