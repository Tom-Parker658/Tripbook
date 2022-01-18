package com.lado.travago.tripbook.ui.agency.config_panel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentAgencyGatewayBinding
import com.lado.travago.tripbook.model.enums.NotificationType
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel
import com.lado.travago.tripbook.ui.agency.config_panel.viewmodel.AgencyConfigViewModel.FieldTags
import com.lado.travago.tripbook.ui.notification.NotificationFragmentArgs
import com.lado.travago.tripbook.utils.UIUtils
import kotlinx.coroutines.*

/**
 * @since 12-01-2022
 * Grants or deny access to a scanner or any person who wants to access his agency
 *
 * (1) ~IS~ "logged in":
 *      + TRUE -> Next(2)
 *      - FALSE -> Inflate "No account found" Notification frag ~THEN~ Inflate "retry button" ~AND~ Show "warning" signs(grey stroke tint & warning icon) on the card
 * (2) ~GET~ "Scanner Doc":
 *      + SUCCESS  -> Next(3)
 *      + NOT_FOUND -> inflate "Become a partner" Notification frag ~THEN~ Inflate "retry button" ~AND~ Show "warning" signs(grey stroke tint & warning icon) on the card
 *      - FAIL -> Inflate error view and toast ~AND~ turn stroke red with error icon ~AND~ inflate retry button
 * (3) ~GET~ "Agency Document"
 *      + SUCCESS -> Inflate Card with Agency Info ~AND~ Inflate Enter button ~AND~ Next(4)
 *      - FAIL ->  Inflate error view and toast ~AND~ turn stroke red with error icon ~AND~ inflate retry button
 * (4) ~IS~ "Administrator scanner"
 *      + TRUE -> Tell activity to inflate more options
 *      - FALSE -> Tell activity to inflate only simple options
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyGatewayFragment : Fragment() {
    private lateinit var viewModel: AgencyConfigViewModel
    private lateinit var binding: FragmentAgencyGatewayBinding
    private lateinit var uiUtils: UIUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_agency_gateway,
            container,
            false)
        viewModel = ViewModelProvider(this)[AgencyConfigViewModel::class.java]

        uiUtils = UIUtils(this, requireActivity(), viewLifecycleOwner)

//        We immediately know if this current booker has an associate profile(We don't want to know if he is even logged in or not at this time)
//        NB: We want to make this false iff the booker at least is logged in but has mo profile and true if he is
//        logged in and has a profile else null
//        viewModel.setField(FieldTags.HAS_PROFILE,
//            uiUtils.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true
//        )
        if (viewModel.isFirstLaunch) {
//            retryProcess()TODO Help
            viewModel.isFirstLaunch = false
        }

        observeLiveData()
        clickListeners()
        return binding.root
    }

    override fun onResume() {
        viewModel.setField(FieldTags.HAS_PROFILE,
            uiUtils.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true
        )
        super.onResume()
    }

    private fun clickListeners() {
        binding.btnEnterAgency.setOnClickListener {
            if (viewModel.scannerDoc.value != null)
                findNavController().navigate(AgencyGatewayFragmentDirections.actionAgencyGatewayFragmentToAgencyConfigCenterFragment())
        }
        binding.btnRetry.setOnClickListener {
            if (viewModel.retryable.value == true) {
//                retryProcess()
                viewModel.setField(FieldTags.RETRYABLE, false)
            }
        }
        binding.btnHelp.setOnClickListener {
            //TODO: Help button for agency gateway
        }
    }

    private fun observeLiveData() {

        viewModel.authRepo.firebaseAuth.addAuthStateListener {
            val args = NotificationFragmentArgs.Builder(NotificationType.ACCOUNT_NOT_FOUND,
                NotificationType.ACCOUNT_NOT_FOUND.toString(), binding.root.id).build()
                .toBundle()
            if (it.currentUser == null) {
                if (args != viewModel.navArgs.value) viewModel.setField(FieldTags.NAV_ARGS, args)
            } else {//When ever the user signs in or signs up and was previously not signed in or signed up, we set the navArg empty
                if (args == viewModel.navArgs.value) viewModel.setField(FieldTags.NAV_ARGS,
                    Bundle.EMPTY)
            }
        }

        viewModel.retryable.observe(viewLifecycleOwner) {
            when (it) {
                true -> binding.btnRetry.visibility = View.VISIBLE
                else -> binding.btnRetry.visibility = View.GONE
                //This signifies the first time the app starts
            }
        }

        viewModel.bookerDoc.observe(viewLifecycleOwner) {
            if (it != null) {
                if (viewModel.hasProfile.value == false)
                    viewModel.setField(FieldTags.HAS_PROFILE, true)
            }
        }
//
        viewModel.hasProfile.observe(viewLifecycleOwner) {
            if (!it && viewModel.authRepo.currentUser != null) {
                val args = NotificationFragmentArgs.Builder(
                    NotificationType.ACCOUNT_NOT_FOUND,
                    NotificationType.ACCOUNT_NOT_FOUND.toString(),
                    R.id.agencyGatewayFragment).build().toBundle()
                viewModel.setField(FieldTags.NAV_ARGS, args)
            }
        }

        viewModel.bookerIsNotScanner.observe(viewLifecycleOwner) {
            if (it && viewModel.hasProfile.value == true && viewModel.authRepo.currentUser != null) {
                val args = NotificationFragmentArgs.Builder(
                    NotificationType.BOOKER_IS_NOT_SCANNER,
                    NotificationType.BOOKER_IS_NOT_SCANNER.toString(),
                    R.id.agencyGatewayFragment
                ).build().toBundle()
                viewModel.setField(FieldTags.NAV_ARGS, args)
            }
        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar4.visibility = View.VISIBLE
            } else {
                binding.progressBar4.visibility = View.GONE
            }
        }
        viewModel.scannerDoc.observe(viewLifecycleOwner) {
            if (it != null) {
                if (viewModel.bookerIsNotScanner.value == true) {
                    viewModel.setField(FieldTags.BOOKER_IS_NOT_SCANNER, false)
                }
                //TODO: We make sure the btnEnterAgency is not yet visible before we do all things so that we do things only once
                if (binding.btnEnterAgency.visibility == View.GONE) {
                    binding.btnHelp.visibility = View.VISIBLE
                    binding.btnEnterAgency.visibility = View.VISIBLE
                    (binding.btnEnterAgency as MaterialButton).setIconResource(R.drawable.baseline_login_24)
                    val text =
                        "Dear ${it.getString("name")},\n You are about to enter your agency. Note that any action you do here is visible to your managers. Also, you are required to keep your device save from any intrusion! Welcome to the SCANNER GATEWAY!!!.\n If this is your first time being a Scanner, press the 'help' button to get help"

                    binding.textWarningGateway.text = text
                    binding.textWarningGateway.visibility = View.VISIBLE
                }
            }
        }
        viewModel.noCachedData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.bookerListener.remove()
                Log.d("Gateway",
                    "You are offline! We will retry automatically when you get back online!")
            }
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
        uiUtils.isConnected.observe(viewLifecycleOwner) {
            //If there was previously no internet and the request failed, we try again
            if (it && viewModel.noCachedData.value == true) {
//                retryProcess()
            }
        }

        viewModel.navArgs.observe(viewLifecycleOwner) {
            if (it != Bundle.EMPTY) {
                findNavController().navigate(R.id.notification_fragment_agency, it)
            }
        }

    }

    /**
     * @since 16-01-2022
     *
     * Just a functions to encapsulate all the request we do to the database
     */
    private fun retryProcess() {
        Log.d("RETRY_PROCESS", "CALLED")
        if (viewModel.noCachedData.value != false)
            viewModel.setField(FieldTags.NO_CACHED_DATA, false)
//
        if (viewModel.authRepo.currentUser != null)
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.bookerDoc(requireContext(),
                    uiUtils)//We retry to get the booker document again
            }
    }
}