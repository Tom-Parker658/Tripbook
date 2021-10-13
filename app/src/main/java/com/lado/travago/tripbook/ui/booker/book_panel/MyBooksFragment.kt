package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentMyBooksBinding
import com.lado.travago.tripbook.databinding.FragmentTripSearchBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.MyBooksViewModel
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
        viewModel = ViewModelProvider(this)[MyBooksViewModel::class.java]
    }

    private fun clickListeners() {
        binding.fabSearchBooks.setOnClickListener { }
        binding.fabBookCalendar.setOnClickListener { }
        binding.fabToolVisibility.setOnClickListener {
            handleFabVisibility()
        }

        //Clears the list containing sorted elements
        binding.fabRevertSort.setOnClickListener {
            viewModel.clearFilters()
            binding.chipBookSortOptions.clearCheck()
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
            if ((it as Chip).isChecked)
                viewModel.sortBooks("price")
            else if (binding.chipBookSortOptions.checkedChipIds.isEmpty()) viewModel.clearFilters()
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

    private fun handleFabVisibility() {
        binding.fabRevertSort.let {
            if (viewModel.sortResultBookList.value!!.isNotEmpty())
                if (it.isShown) it.hide() else it.show()
        }
        binding.fabBookCalendar.let {
            if (it.isShown) it.hide() else it.show()
        }
        binding.fabSearchBooks.let {
            if (it.isShown) it.hide() else it.show()
        }
        binding.fabToolVisibility.let {
            if (binding.fabSearchBooks.isShown) {
                it.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                it.setImageResource(R.drawable.baseline_visibility_24)
            }
        }
    }

    private fun observeLiveData() {
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
            if (it.isNotEmpty()) {
                setRecycler(it)
            }
        }
        //If the sorted list is empty, we show all the original elements(books) in their original order
        viewModel.sortResultBookList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //We make visible the revertSort Fab
                binding.fabRevertSort.show()
                setRecycler(it)
            } else {
                binding.fabRevertSort.hide()
                if (viewModel.allMyBooks.value!!.isNotEmpty())
                    setRecycler(viewModel.allMyBooks.value!!)
            }
        }
    }

    private fun setRecycler(bookList: List<DocumentSnapshot>) {
        //Adapter
        adapter = SummaryItemAdapter(
            clickListener = SummaryItemClickListener {
                //TODO: Show the book in detail
            }
        )
        val bookSummaryList = SummaryItem.createSummaryItemsFromBooks(bookList)
        adapter.submitList(bookSummaryList)
        //Recycler View
        val linearManager = LinearLayoutManager(requireContext())
        binding.booksRecyclerView.layoutManager = linearManager
        binding.booksRecyclerView.adapter = adapter
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
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_textview,
                townNames
            )
            (searchBinding.editTextLocality.editText as AutoCompleteTextView).setAdapter(adapter)
            (searchBinding.editTextDestination.editText as AutoCompleteTextView).setAdapter(adapter)
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
                                "locality",
                                searchBinding.editTextLocality.editText!!.toString()
                            )
                            it.whereEqualTo(
                                "destination",
                                searchBinding.editTextDestination.editText!!.toString()
                            )
                        }
                    else if (searchBinding.editTextLocality.editText!!.toString().isNotBlank())
                        viewModel.searchBooks {
                            it.whereEqualTo(
                                "locality",
                                searchBinding.editTextLocality.editText!!.toString()
                            )
                        }
                    else if (searchBinding.editTextDestination.editText!!.toString().isNotBlank())
                        viewModel.searchBooks {
                            it.whereEqualTo(
                                "destination",
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

}