package com.lado.travago.tripbook.ui.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentNotificationBinding
import com.lado.travago.tripbook.model.enums.NotificationType
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.ui.booker.book_panel.BooksActivity
import com.lado.travago.tripbook.ui.booker.book_panel.TripSearchActivity
import com.lado.travago.tripbook.utils.UIUtils
import com.lado.travago.tripbook.utils.contracts.BookerSignUpContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


/**
 * A simple [Fragment] to show notifications, instructions, error messages etc
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_notification,
            container,
            false
        )
        /**
         * We want to avoid by all means to be on this notification fragment when the booker is already signed in and we still show him/her that he needs to signup still
         */
        if (NotificationFragmentArgs.fromBundle(requireArguments()).callerResID ==
            R.id.agencyGatewayFragment
        ) {
            val uiUtil = UIUtils(this, requireActivity(), viewLifecycleOwner)
            if (uiUtil.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true && FirebaseAuthRepo().currentUser != null) {
                findNavController().navigateUp()
            } else if (uiUtil.getSharedPreference(UIUtils.SP_BOOL_BOOKER_PROFILE_EXIST) == true) {
                findNavController().navigateUp()
            }
        }
        getNotificationObject(NotificationFragmentArgs.fromBundle(requireArguments()).notificationType)

        Log.d("NotificationFragment", "Called!")
        return binding.root
    }

    private fun getNotificationObject(
        type: NotificationType,
    ) {
        when (type) {
            /**
             * [hasNoProfile] is true if the booker has an account, but has not created a profile BUT
             * is false when the booker doesn't have an account at all
             */
            //We create an account or let the user logIn
            NotificationType.ACCOUNT_NOT_FOUND -> {
                val hasNoProfile =
                    (FirebaseAuthRepo().currentUser != null)//In this case, the booker is signed in but hasn't created a profile yet
                binding.imgNotif.setImageResource(if (!hasNoProfile) R.drawable.baseline_login_24 else R.drawable.baseline_person_24)
                binding.textNotifTitle.text =
                    if (!hasNoProfile) "No Tripbook account found!" else "No Profile found!"
                binding.btnNotifPositive.text =
                    if (!hasNoProfile) "SignUp/Create" else "Create profile"
                binding.textNotifMessage.text = if (!hasNoProfile)
                    "You must posses a Tripbook account before continuing. If you already have an account or you want to create one, please tap the button below! Thanks"
                else
                    "You posses a Tripbook account but lack a profile. To continue, you must create a profile"
                binding.btnNotifPositive.setOnClickListener {
                    startBookerCreationActivity()
                }
            }
            //We want to display a congratulation message and sent the booker to go and check his books
            NotificationType.BOOKING_COMPLETE -> {
                binding.imgNotif.setImageResource(R.drawable.baseline_cake_24)
                binding.textNotifTitle.text = "Congrats!! Booking complete"
                binding.btnNotifPositive.text = "My books"
                binding.btnNotifPositive.setOnClickListener {
                    startActivity(Intent(requireContext(), BooksActivity::class.java))
                }
                //We transform the "negative button" to show the continue booking option
                binding.btnNotifNegative.apply {
                    (this as MaterialButton)
                    this.setBackgroundColor(resources.getColor(R.color.colorNeutralButton))
                    this.text = "Continue booking"
                    this.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    this.setIconResource(R.drawable.baseline_arrow_back_24)
                    this.visibility = View.VISIBLE
                }
                binding.btnNotifNegative.setOnClickListener {
                    startActivity(Intent(requireContext(), TripSearchActivity::class.java))
                }
                //We get all info to inflate the message
                binding.textNotifMessage.text =
                    "Nice shot! Your transactions were successful.\n You can now tap on 'My books' to have a view of your QR-Code or 'Continue booking' to explore more. "

                //Nice possibility
                /*TripDetailsFragmentArgs.fromBundle()
                binding.textNotifMessage =
                    "Nice shot! You have successfully booked ${numberOfBooks} book${if (numberOfBooks > 1) "s" else ""} from ${localityName} to ${destinationName} programmed for ${
                        Utils.formatDate(
                            dateInMillis,
                            "EEEE dd MMMM yyyy"
                        )
                    } at ${
                        TimeModel.from24Format(hour, minutes)
                            .formattedTime(TimeModel.TimeFormat.FORMAT_12H)
                    }\n You can now tap on 'See my book' to have a view of your QR-Code or 'Continue booking' to explore more.  "*/
            }
            //Trip Not found
            NotificationType.EMPTY_RESULTS -> {
                //We want to remove everything
                val callerResID =
                    NotificationFragmentArgs.fromBundle(requireArguments()).callerResID

                binding.imgNotif.setImageResource(R.drawable.not_found_24)
                binding.textNotifTitle.text = getString(R.string.text_empty_content)
                binding.btnNotifPositive.text = "Notify us"
                binding.btnNotifPositive.setOnClickListener {
                    startActivity(Intent(requireContext(), BooksActivity::class.java))
                }
//                //We transform the "negative button" to show the continue booking option
//                binding.btnNotifNegative.apply {
//                    (this as MaterialButton)
//                    this.setBackgroundColor(resources.getColor(R.color.colorNeutralButton))
//                    this.text = "Go back"
//                    this.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
//                    this.setIconResource(R.drawable.baseline_arrow_back_24)
//                    this.visibility = View.VISIBLE
//                    setOnClickListener {
//                        if (callerResID == R.layout.fragment_trip_search_result) {
//                            findNavController().getBackStackEntry(R.id.tripSearchFragment)
//                        }
//                    }
                // }
                //We get all info to inflate the message
                binding.textNotifMessage.text =
                    "Sorry, no trip was found!\n You may notify to us so that the trip can be added in the nearest future or you may go back and search for a different trip. Thanks"

            }
            //Booker is not a scanner
            NotificationType.BOOKER_IS_NOT_SCANNER -> {
                val callerResID =
                    NotificationFragmentArgs.fromBundle(requireArguments()).callerResID

                binding.imgNotif.setImageResource(R.drawable.outline_add_agency_24)
                binding.textNotifTitle.text = "Become a Tripbook Partner"

                binding.btnNotifPositive.setOnClickListener {
                    findNavController().navigate(R.id.agencyProfileFragment)
                }
                binding.btnNotifPositive.text = "Create an agency"
                (binding.btnNotifPositive as MaterialButton).setIconResource(R.drawable.baseline_add_24)

                binding.textNotifMessage.text =
                    "You neither a 'Tripbook Agency Owner' nor a 'Tripbook Agency Scanner'. You may create an agency below and become our partner. \nNB: If you are an employee of any agency, notify them and make sure they add you as their Scanner.Thanks!"
            }
        }


    }

    private val bookerCreationContract =
        registerForActivityResult(BookerSignUpContract()) { resultCode ->
            when (resultCode) {
                Activity.RESULT_OK -> {
                    displayMessage("Congrats you are now signedUp!! You can please continue.")
                    findNavController().navigateUp()
                    onDestroyView()
                }
                Activity.RESULT_CANCELED -> {
                    displayMessage("You cancelled!")
                }
                else -> displayMessage("We're sorry, something went wrong. Please try again.")
            }
        }

    private fun startBookerCreationActivity()
    //To start the booker signUp or LogIn if the user hasn't logIn already
    {
        bookerCreationContract.launch(
            SignUpCaller.OTHER_ACTIVITY
        )
    }


    private fun displayMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).apply {
            setGravity(1, 0, 20)
            show()
        }
    }

    fun InflateViewForEmptyResults() {
        val view = binding.apply {
            this.imgNotif.setImageResource(R.drawable.not_found_24)
            this.textNotifTitle.text = "No results (>_<)"
//            this.btnNotifPositive.text = ""
        }
    }

}