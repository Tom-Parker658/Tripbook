package com.lado.travago.tripbook.ui.agency.config_panel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentScannerConfigBinding
import com.lado.travago.tripbook.databinding.LayoutScannerSearchBinding
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.ScannerConfigViewModel
import com.lado.travago.tripbook.ui.recycler_adapters.ScannerConfigAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.ScannerConfigClickListener
import com.lado.travago.tripbook.utils.loadImageFromUrl
import kotlinx.coroutines.*

/**
 * Here the agency administrators can add other scanners
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class ScannerConfigFragment : Fragment() {
    private lateinit var parentViewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentScannerConfigBinding
    private lateinit var viewModel: ScannerConfigViewModel
    private lateinit var adapter: ScannerConfigAdapter
    /**
     * Inorder to stop any loading blocking the ui
     */
    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentViewModel = ViewModelProvider(requireActivity())[AgencyConfigViewModel::class.java]
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_scanner_config,
            container,
            false
        )
        viewModel = ViewModelProvider(this)[ScannerConfigViewModel::class.java]
        observeLiveData()
        try {
            setRecycler()
        } catch (e: Exception) {
            //Nothing
        }
        fabListeners()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setRecycler() {
        binding.scannerRecycler.also {
            it.layoutManager = GridLayoutManager(context, 2)
            it.adapter = adapter
        }
    }

    private fun observeLiveData() {
        viewModel.retrySearch.observe(viewLifecycleOwner) {
            if (it) CoroutineScope(Dispatchers.Main).launch {
                viewModel.getScannerListData(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
        }
        viewModel.myScannerList.observe(viewLifecycleOwner) {
            adapter = ScannerConfigAdapter(
                clickListener = ScannerConfigClickListener { scannerID, scannerButtonTag ->
                    when (scannerButtonTag) {
                        ScannerConfigViewModel.ScannerButtonTags.BUTTON_IS_ADMIN -> {
                            viewModel.makeScannerAdmin(scannerID)
                            //We rebind the item
                            val scannerDoc = viewModel.myScannerList.value!!.find { doc ->
                                doc.id == scannerID
                            }
                            adapter.notifyItemChanged(
                                viewModel.myScannerList.value!!.indexOf(
                                    scannerDoc!!
                                )
                            )
                        }
                        ScannerConfigViewModel.ScannerButtonTags.BUTTON_FIRE_SCANNER -> {
                            MaterialAlertDialogBuilder(requireContext()).apply {
                                setTitle("Warning!!!")
                                setMessage("Are you sure you want to fire this scanner from your agency? ")
                                setPositiveButton("Fire the Scanner") { dialog, _ ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        viewModel.fireScanner(scannerId = scannerID,
                                            agencyID = parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                                        )
                                    }
                                    dialog.dismiss()
                                    dialog.cancel()
                                }
                            }.create().apply {
                                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                            }.show()
                        }
                    }
                }, viewModel.adminIDList as List<HashMap<String, Any>>
            )
            setRecycler()
            adapter.submitList(it)
            viewModel.setField(ScannerConfigViewModel.FieldTags.ON_LOADING, false)

        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                Log.d("SCANNER", it)
                viewModel.setField(ScannerConfigViewModel.FieldTags.TOAST_MESSAGE, "")
            }
        }
        viewModel.onScannerFired.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.notifyItemRemoved(it)
                viewModel.setField(ScannerConfigViewModel.FieldTags.ON_SCANNER_FIRED, null)
            }
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.onAddScannerDialog.observe(viewLifecycleOwner) {
            if (it) AddScannerDialogFragment(viewModel, parentViewModel).showNow(
                childFragmentManager,
                "DIALOG MANAGER"
            )
        }
        viewModel.onClose.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    TripsConfigFragmentDirections.actionTripsConfigFragmentToTownsConfigFragment()
                )
                viewModel.setField(ScannerConfigViewModel.FieldTags.ON_CLOSE, false)
                viewModel.setField(ScannerConfigViewModel.FieldTags.RETRY_SEARCH, true)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
        } catch (e: Exception) {
            Log.d("ScannerConfigFragment", e.message.toString())
            //TODO: Truly handle this error please future Lado
        }
    }

    private fun fabListeners() {
        binding.fabAddScanner.setOnClickListener {
            viewModel.setField(ScannerConfigViewModel.FieldTags.ADD_SCANNER_DIALOG, true)
        }
        binding.fabSort.setOnClickListener {
            //1-We create a spinner with options
            MaterialAlertDialogBuilder(requireContext()).apply {
                setIcon(R.drawable.baseline_sort_24)
                setTitle("Sort By?")
                setSingleChoiceItems(
                    arrayOf("None", "Name", "Number of Scans", "Recruitment date", "Administrator"),
                    viewModel.sortCheckedItem
                ) { dialog, which ->
                    when (which) {
                        1 -> {
                            viewModel.sortResult(ScannerConfigViewModel.SortTags.SCANNER_NAME)
                            viewModel.setField(ScannerConfigViewModel.FieldTags.CHECKED_ITEM, 1)
                        }
                        2 -> {
                            viewModel.sortResult(ScannerConfigViewModel.SortTags.NUM_SCANS)
                            viewModel.setField(ScannerConfigViewModel.FieldTags.CHECKED_ITEM, 2)
                        }
                        3 -> {
                            viewModel.sortResult(ScannerConfigViewModel.SortTags.ADDED_ON)
                            viewModel.setField(ScannerConfigViewModel.FieldTags.CHECKED_ITEM, 3)
                        }
                        4 -> {
                            viewModel.sortResult(ScannerConfigViewModel.SortTags.IS_ADMIN)
                            viewModel.setField(ScannerConfigViewModel.FieldTags.CHECKED_ITEM, 4)
                        }
                    }
                    dialog.dismiss()
                    adapter.notifyDataSetChanged()
                }
            }.create().show()
        }
        binding.btnSaveScanners.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.uploadAdmin(parentViewModel.bookerDoc.value!!.getString("agencyID")!!)
            }
        }
    }

    class AddScannerDialogFragment(val viewModel: ScannerConfigViewModel, private val parentViewModel: AgencyConfigViewModel) : DialogFragment() {
        @SuppressLint("DialogFragmentCallbacksDetector")
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            val scannerBinding: LayoutScannerSearchBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.layout_scanner_search,
                null,
                true
            )

            initDialogDetails(scannerBinding)

            return MaterialAlertDialogBuilder(requireContext())
                // Add customization options here
                .setTitle("Recruit Scanner")
                .setIcon(R.drawable.baseline_person_add_24)
                .setView(scannerBinding.root)
                .create()
        }

        private fun initDialogDetails(scannerBinding: LayoutScannerSearchBinding) {
            if (viewModel.scannerPhone.isNotBlank())
                scannerBinding.phoneField.editText!!.setText(
                    viewModel.scannerPhone
                )

            scannerBinding.countryCodeField.registerCarrierNumberEditText(scannerBinding.phoneField.editText)// Phone format
            scannerBinding.countryCodeField.setCountryForPhoneCode(viewModel.countryCode)

            scannerBinding.phoneField.editText!!.addTextChangedListener {
                if (scannerBinding.countryCodeField.isValidFullNumber)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.searchNewScanner(
                            scannerBinding.countryCodeField.fullNumberWithPlus
                        )
                        viewModel.setField(
                            ScannerConfigViewModel.FieldTags.SCANNER_PHONE,
                            scannerBinding.phoneField.editText!!.text.toString()
                        )
                    }
                else {
                    viewModel.setField(
                        ScannerConfigViewModel.FieldTags.ON_SEARCH_LOADING,
                        false
                    )
                    scannerBinding.scannerCard.visibility = View.GONE
                }
            }
            scannerBinding.btnRecruitScanner.setOnClickListener {
                if ((scannerBinding.btnRecruitScanner as MaterialButton).text == "Recruit")
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.recruitScanner(
                            viewModel.newScannerDoc.value!!,
                            parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                        )
                    }
                else
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.fireScanner(
                            viewModel.newScannerDoc.value!!.id, parentViewModel.bookerDoc.value!!.getString("agencyID")!!
                        )
                    }

                dismiss()
            }
            viewModel.onSearchLoading.observe(this) {
                if (it) scannerBinding.searchProgressBar.visibility = View.VISIBLE
                else scannerBinding.searchProgressBar.visibility = View.GONE
            }

            viewModel.newScannerDoc.observe(this) {
                scannerBinding.searchProgressBar.visibility = View.GONE
                if (it.exists()) {
                    //If he is already a scanner
                    if (viewModel.myScannerList.value!!.find { doc ->
                            doc.id == viewModel.newScannerDoc.value!!.id
                        } != null) {
                        //We instead fire a scanner if he is already employed
                        (scannerBinding.btnRecruitScanner as MaterialButton).apply {
                            text = "Fire!"
                            setIconResource(R.drawable.round_cancel_24)
                            setBackgroundColor(requireActivity().resources.getColor(R.color.colorNegativeButton))
                        }
                    } else {//Not found in this list, we recruit
                        (scannerBinding.btnRecruitScanner as MaterialButton).apply {
                            text = "Recruit"
                            setIconResource(R.drawable.baseline_add_24)
                            setBackgroundColor(requireActivity().resources.getColor(R.color.colorPositiveButton))
                        }
                    }
                    //We inflate with data
                    scannerBinding.scannerCard.visibility = View.VISIBLE
                    scannerBinding.scannerPhoto.loadImageFromUrl(it.getString("photoUrl")!!)
                    scannerBinding.textScannerPhone.text = it.getString("phone")
                    scannerBinding.textScannerName.text = it.getString("name")
                }
            }
            viewModel.onNoResult.observe(this) {
                //TODO: On No results found
            }
        }

        override fun onDismiss(dialog: DialogInterface) {
            viewModel.setField(ScannerConfigViewModel.FieldTags.ADD_SCANNER_DIALOG, false)
            super.onDismiss(dialog)
        }
    }

}