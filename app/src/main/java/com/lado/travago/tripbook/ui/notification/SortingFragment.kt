
package com.lado.travago.tripbook.ui.notification

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.LayoutSortItemsBinding
import com.lado.travago.tripbook.utils.AdminUtils
import com.lado.travago.tripbook.utils.AdminUtils.SortingParams.*
import com.lado.travago.tripbook.utils.AdminUtils.sortDocuments


/**
 * A simple [Fragment] subclass.
 * Use the [SortingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SortingFragment(
    val list: MutableList<DocumentSnapshot>,
    private val sortOptions: List<AdminUtils.SortOption>,
    private var currentSelection: Int? = null
) : DialogFragment() {

    lateinit var binding: LayoutSortItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.layout_sort_items, container, false)
        showOnlyNecessarySortOptions()

        binding.chipGroupSorts.setOnCheckedChangeListener { group: ChipGroup, checkedId: Int ->
            //We don't accept reselection
            if (checkedId != currentSelection) {
                val selectedParam = when (checkedId) {
                    binding.chipSortPopularityAsc.id -> POPULARITY_ASC
                    binding.chipSortPopularityDesc.id -> POPULARITY_DESC

                    binding.chipSortReputationAsc.id -> REPUTATION_ASC
                    binding.chipSortReputationDesc.id -> REPUTATION_DESC

                    binding.chipSortNameAsc.id -> NAMES_ASC
                    binding.chipSortNameDesc.id -> NAMES_DESC

                    binding.chipSortPriceAsc.id -> PRICE_ASC
                    binding.chipSortPriceDesc.id -> PRICE_DESC

                    binding.chipSortRegionAsc.id -> REGION_ASC
                    binding.chipSortRegionDesc.id -> REGION_DESC

                    binding.chipSortVipsFirst.id -> VIP_FIRST
                    binding.chipSortVipsLast.id -> VIP_LAST
                    binding.chipSortTaken.id -> TAKEN_ALREADY
//                    binding.chipSortExpiredFirst.id -> EXPIRY_FIRST
                    else -> EXPIRY_FIRST
                }
                //We are sure it will never be null
                val selectedOption = sortOptions.find { it.sortingParam == selectedParam }!!
                list.sortDocuments(selectedOption)
            }
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            // Add customization options here
            .setView(binding.root)
            .setNegativeButton(R.string.text_cancel) { dialog, _ ->
                currentSelection = null
                clearAllChecks()
                dialog.cancel()
                dialog.dismiss()
                //TODO: Clear all filters and set the list to its original state if possible
            }
            .create()

        return dialog
    }


    /**
     * This method is always upon creating the view. The caller will need to specify the options he wants to show
     * [sortOptions] is the list which will contain all options adpated to the caller.
     */
    private fun showOnlyNecessarySortOptions() {
        sortOptions.forEach {
            when (it.sortingParam) {
                POPULARITY_ASC -> binding.chipSortPopularityAsc.visibility = View.VISIBLE
                POPULARITY_DESC -> binding.chipSortPopularityDesc.visibility = View.VISIBLE

                REPUTATION_ASC -> binding.chipSortReputationAsc.visibility = View.VISIBLE
                REPUTATION_DESC -> binding.chipSortReputationDesc.visibility = View.VISIBLE

                NAMES_ASC -> binding.chipSortNameAsc.visibility = View.VISIBLE
                NAMES_DESC -> binding.chipSortNameDesc.visibility = View.VISIBLE

                PRICE_ASC -> binding.chipSortPriceAsc.visibility = View.VISIBLE
                PRICE_DESC -> binding.chipSortPriceDesc.visibility = View.VISIBLE

                REGION_ASC -> binding.chipSortRegionAsc.visibility = View.VISIBLE
                REGION_DESC -> binding.chipSortRegionDesc.visibility = View.VISIBLE

                VIP_FIRST -> binding.chipSortVipsFirst.visibility = View.VISIBLE
                VIP_LAST -> binding.chipSortVipsLast.visibility = View.VISIBLE

                TAKEN_ALREADY -> binding.chipSortTaken.visibility = View.VISIBLE
                EXPIRY_FIRST -> binding.chipSortExpiredFirst.visibility = View.VISIBLE
            }
        }
    }

    private fun clearAllChecks() = binding.chipGroupSorts.clearCheck()


}