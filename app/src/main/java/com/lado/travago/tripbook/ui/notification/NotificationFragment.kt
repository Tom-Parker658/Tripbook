package com.lado.travago.tripbook.ui.notification

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.util.Util
import com.google.android.material.button.MaterialButton
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentNotificationBinding
import com.lado.travago.tripbook.model.admin.TimeModel
import com.lado.travago.tripbook.model.enums.NotificationType
import com.lado.travago.tripbook.ui.booker.book_panel.BooksActivity
import com.lado.travago.tripbook.ui.booker.book_panel.TripDetailsFragmentArgs
import com.lado.travago.tripbook.ui.booker.book_panel.TripSearchActivity
import com.lado.travago.tripbook.utils.Utils
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
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_notification,
            container,
            false
        )
        getNotificationObject(NotificationFragmentArgs.fromBundle(requireArguments()).notificationType)
        return binding.root
    }

    private fun getNotificationObject(
        type: NotificationType,
    ) {
        when (type) {
            //We create an account or let the user logIn
            NotificationType.ACCOUNT_NOT_FOUND -> {
                binding.imgNotif.setImageResource(R.drawable.baseline_login_24)
                binding.textNotifTitle.text = "No Tripbook account found!"
                binding.btnNotifPositive.text = "SignUp/Create"
                binding.textNotifMessage.text =
                    "You must posses a Tripbook account before continuing. If you already have an account or you want to create one, please tap the button below! Thanks"
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
                binding.imgNotif.setImageResource(R.drawable.not_found_24)
                binding.textNotifTitle.text = getString(R.string.text_message_not_found_drop_down)
                binding.btnNotifPositive.text = "Notify us"
                binding.btnNotifPositive.setOnClickListener {
//                    startActivity(Intent(requireContext(), BooksActivity::class.java))
//
                }
                //We transform the "negative button" to show the continue booking option
                binding.btnNotifNegative.apply {
                    (this as MaterialButton)
                    this.setBackgroundColor(resources.getColor(R.color.colorNeutralButton))
                    this.text = "Go back"
                    this.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    this.setIconResource(R.drawable.baseline_arrow_back_24)
                    this.visibility = View.VISIBLE
                    setOnClickListener {
                        findNavController().navigateUp()
                    }
                }
                //We get all info to inflate the message
                binding.textNotifMessage.text =
                    "Sorry, no trip was found!\n You may notify to us so that the trip can be added in the nearest future or you may go back and search for a different trip. Thanks"

            }
        }

    }

    private val bookerCreationContract =
        registerForActivityResult(BookerSignUpContract()) { resultCode ->
            when (resultCode) {
                Activity.RESULT_OK -> {
                    displayMessage("Congrats!! You can please continue.")
                    findNavController().navigateUp()
                    onDestroyView()
                }
                Activity.RESULT_CANCELED -> {}
                else -> displayMessage("We're sorry, something went wrong. Please try again.")
            }
        }

    private fun startBookerCreationActivity() {
        //To start the booker signUp or LogIn if the user hasn't logIn already
        bookerCreationContract.launch(
            Bundle.EMPTY
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
            this.textNotifTitle.text = "No results :<("
//            this.btnNotifPositive.text = ""
        }
    }

}