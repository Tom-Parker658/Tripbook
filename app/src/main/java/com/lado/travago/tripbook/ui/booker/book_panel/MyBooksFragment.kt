package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentMyBooksBinding
import com.lado.travago.tripbook.databinding.FragmentTripSearchBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.MyBooksViewModel
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.MyBooksViewModel.*
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * Booked ticket list fragment
 */
@ExperimentalCoroutinesApi
class MyBooksFragment : Fragment() {
    private lateinit var binding: FragmentMyBooksBinding
    private lateinit var viewModel: MyBooksViewModel
    private lateinit var adapter: SummaryItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_my_books,
            container,
            false
        )
        initViewModel()
        clickListeners()
        observeLiveData()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[MyBooksViewModel::class.java]
    }

    private fun clickListeners() {
        binding.fabSearchBooks.setOnClickListener { searchBooks() }
        binding.fabBookCalendar.setOnClickListener { singleDatePicker() }
        binding.fabToolVisibility.setOnClickListener {
            viewModel.invertFabVisibility()
        }

        //Clears the list containing sorted elements
        binding.fabRevertSort.setOnClickListener {
            binding.chipBookSortOptions.clearCheck()
            viewModel.clearFilters()
            viewModel.invertFabVisibility()
            viewModel.invertFabVisibility()
        }
        binding.chipBookFilterDepartureDate.setOnClickListener {
            if ((it as Chip).isChecked)
                viewModel.sortBooks("tripDateInMillis")
            else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
        }
        binding.chipBookFilterDistance.setOnClickListener {
            if ((it as Chip).isChecked)
                viewModel.sortBooks("distance")
            else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
        }
        binding.chipBookFilterPrice.setOnClickListener {
            if ((it as Chip).isChecked) {
                //We prioritize the price the user needs either vip or normal
                viewModel.sortBooks("price")
            } else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
        }
        binding.chipBookFilterExpiredFirst.setOnClickListener {
            if ((it as Chip).isChecked)
                viewModel.sortBooks("isExpired")
            else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
        }
        binding.chipBookFilterVipFirst.setOnClickListener {
            if ((it as Chip).isChecked)
                viewModel.sortBooks("isVip")
            else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
        }

    }

    private fun observeLiveData() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG)
                    .show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.notFound.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.setField(FieldTags.TOAST_MESSAGE, "Not found")
            }
        }
        viewModel.retry.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it) viewModel.getAllBooks()
            }
        }
        viewModel.fabVisibilityState.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabSearchBooks.show()
                binding.fabBookCalendar.show()
                binding.fabToolVisibility.setImageResource(R.drawable.baseline_visibility_off_24)
                if (viewModel.sortResultBookList.value!!.isNotEmpty())
                    binding.fabRevertSort.show()

            } else {
                binding.fabSearchBooks.hide()
                binding.fabBookCalendar.hide()
                binding.fabToolVisibility.setImageResource(R.drawable.baseline_visibility_24)
                if (viewModel.sortResultBookList.value!!.isNotEmpty())
                    binding.fabRevertSort.hide()
            }
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.bookProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                )

            } else {
                binding.bookProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.allMyBooks.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) setRecycler(it)
        }
        //If the sorted list is empty, we show all the original elements(books) in their original order
        viewModel.sortResultBookList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setRecycler(it)
                binding.fabRevertSort.show()
            } else {
                binding.fabRevertSort.hide()
                setRecycler(viewModel.allMyBooks.value!!)
            }
        }
    }

    private fun setRecycler(bookList: List<DocumentSnapshot>) {
        //Adapter
        adapter = SummaryItemAdapter(
            clickListener = SummaryItemClickListener {
                //We navigate to the book details
                findNavController().navigate(
                    MyBooksFragmentDirections.actionMyBooksFragmentToBookDetailsFragment(it.id)
                )
            }
        )
        val bookSummaryList = SummaryItem.createSummaryItemsFromBooks(bookList)
        adapter.submitList(bookSummaryList)
        //Recycler View
        val linearManager = LinearLayoutManager(requireContext())
        binding.booksRecyclerView.layoutManager = linearManager
        binding.booksRecyclerView.adapter = adapter
        binding.booksRecyclerView.invalidate()
        adapter.notifyDataSetChanged()
    }

    private fun searchBooks() {
        val searchBinding = FragmentTripSearchBinding.inflate(layoutInflater)
        searchBinding.apply {
            btnSearchJourney.visibility = View.GONE
            textTripbook.visibility = View.GONE
            editTextTime.visibility = View.GONE
            editTextDates.visibility = View.GONE

            val townNames = resources.getStringArray(R.array.localities).toList()
            val localityAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_textview,
                townNames
            )
            val destinationAdapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_textview,
                townNames
            )
            (searchBinding.editTextLocality.editText as AutoCompleteTextView).setAdapter(
                localityAdapter
            )
            (searchBinding.editTextDestination.editText as AutoCompleteTextView).setAdapter(
                destinationAdapter
            )
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.text_dialog_search)
            .setView(searchBinding.root)
            .setPositiveButton(R.string.text_dialog_search) { dialog, _ ->
                searchBinding.progressBookSearch.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    if (searchBinding.editTextLocality.editText!!.toString()
                            .isNotBlank() && searchBinding.editTextDestination.editText!!.toString()
                            .isNotBlank()
                    )
                        viewModel.searchBooks {
                            it.whereEqualTo(
                                "tripLocalityName",
                                searchBinding.editTextLocality.editText!!.toString()
                            )
                            it.whereEqualTo(
                                "tripDestinationName",
                                searchBinding.editTextDestination.editText!!.toString()
                            )
                        }
                    else if (searchBinding.editTextLocality.editText!!.toString().isNotBlank())
                        viewModel.searchBooks {
                            it.whereEqualTo(
                                "tripLocalityName",
                                searchBinding.editTextLocality.editText!!.toString()
                            )
                        }
                    else if (searchBinding.editTextDestination.editText!!.toString()
                            .isNotBlank()
                    )
                        viewModel.searchBooks {
                            it.whereEqualTo(
                                "tripDestinationName",
                                searchBinding.editTextDestination.editText!!.toString()
                            )
                        }
                    else dialog.dismiss()
                }.invokeOnCompletion {
                    searchBinding.progressBookSearch.visibility = View.GONE
                }
            }
            .create()
            .show()
    }

    private fun singleDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Search your books for a particular day!")
            .build()

        picker.addOnNegativeButtonClickListener {
            //We call the range picker when the single date picker is closed
            //I did this for now only
            rangeDatePicker()
        }
        picker.addOnPositiveButtonClickListener { selectedDate ->
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.searchBooks {
                    it.whereEqualTo("tripDateInMillis", selectedDate)
                }
            }
        }
        picker.showNow(this.childFragmentManager, "Date picker")

    }

    //Permit the booker to see books booked only in a particular interval
    private fun rangeDatePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("See your books within a particular interval!")
            .build()
        picker.addOnPositiveButtonClickListener { datePair ->
            CoroutineScope(Dispatchers.Main).launch {
                if (datePair.first >= datePair.second)
                    viewModel.searchBooks {
                        it.whereLessThanOrEqualTo("tripDateInMillis", datePair.first)
                        it.whereGreaterThanOrEqualTo("tripDateInMillis", datePair.second)
                    }
                else viewModel.searchBooks {
                    it.whereLessThanOrEqualTo("tripDateInMillis", datePair.second)
                    it.whereGreaterThanOrEqualTo("tripDateInMillis", datePair.first)
                }
            }
        }
        picker.addOnNegativeButtonClickListener { }
        picker.showNow(this.childFragmentManager, "Range picker")
    }


}