package com.lado.travago.tripbook.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.ui.agency.config_panel.AgencyConfigActivity
import com.lado.travago.tripbook.ui.booker.book_panel.BooksActivity
import com.lado.travago.tripbook.ui.booker.book_panel.TripSearchActivity
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import com.google.android.material.textview.MaterialTextView
import com.lado.travago.tripbook.databinding.ElementNetworkStateHeaderBinding
import com.lado.travago.tripbook.model.admin.TimeModel


/**
 * @since 11-01-2022
 *
 * Contains UI-related utilities or function e.g Widget animations etc
 * Created to avoid repeating CODE in activities and fragments
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class UIUtils(
    private val hostFragment: Fragment?,
    private val hostActivity: Activity,
    private val lifecycleOwner: LifecycleOwner,
) {
    /**
     * Meant to watch over the phone access to internet
     */
    private var _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    /**<---------------------------Inner Functions---------------------->**/
    /**
     * @since 11-01-2022
     * Control bottom nav bar disappearance and appearance from Fragments and Activities.
     *
     * @param show TRUE to show the bottom nav bar else HIDE
     * @param bottomNavView The Bottom navigation bar from the activity view
     */
    fun bottomBarVisibility(show: Boolean, bottomNavView: BottomNavigationView) {
        if (show != (bottomNavView.isVisible)) {
            val animResID = if (show) R.anim.slide_up else R.anim.slide_down
            val animation =
                AnimationUtils.loadAnimation(hostActivity, animResID).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {}
                        override fun onAnimationEnd(p0: Animation?) {
                            if (show)//NB: Note the conditions
                                bottomNavView.visibility = View.VISIBLE
                            else
                                bottomNavView.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}
                    }
                    )
                }
            bottomNavView.animation = animation
        }
    }

    /**
     * @since 12-01-2022
     *
     * *Controls navigation between all the activities
     * @param currentActivityAction is the resource ID to be selected on the bottom bar. e.g R.id.action_agency_config
     */
    fun activityNavigation(bottomNavView: BottomNavigationView, @IdRes currentActivityAction: Int) {
        hostActivity.apply {
            bottomNavView.selectedItemId = currentActivityAction
            bottomNavView.setOnItemSelectedListener {
                when (it.itemId) {
                    currentActivityAction -> {
                        //DO nothing
                        true
                    }

                    R.id.action_trip_search -> {
                        startActivity(
                            Intent(this, TripSearchActivity::class.java)
                        )
                        false
                    }
                    R.id.action_agency_config -> {
                        startActivity(
                            Intent(this, AgencyConfigActivity::class.java)
                        )
                        false
                    }

                    R.id.action_my_books -> {
                        startActivity(
                            Intent(this, BooksActivity::class.java)
                        )
                        false
                    }
                    R.id.action_booker_help -> {
                        false
                    }
                    R.id.action_booker_account -> {
                        startActivity(
                            Intent(this, BookerCreationActivity::class.java)
                        )
                        false
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    //TODO: Implement this all over the app

    /**
     * @since 13-01-2022
     *
     */

    /**
     * @since 12-01-2022
     *
     *  We use a counter with a step length of 2seconds and after each tick, we request the network
     *  status and update our [_isConnected] variable. The counter has a life time of One hour and after it
     *  ends, we renew it again to 1 hour (NB: We don't want to create an infinitely long timer)
     *  The observer of [_isConnected] then checks if this a new state compared to that gotten and saved earlier
     *  (NB: If the state is negative i.e no internet, we call it a new state). If the state has changed,
     *  we update the layouts accordingly to the state else, if there is no state change, we VERIFY
     *  if it means we are online; in that case, we make the "You are online" text invisible(i.e, If the
     *  User becomes online, we just show it for 2 seconds then we make it disappear but if there is no internet connection,
     *  we keep the "You are offline" text till the state changes)
     *
     *  NB We decided to use a counter since other ways of implementing a life listener are above API 19
     *
     */
    fun onNetworkChange(headerBinding: ElementNetworkStateHeaderBinding) {
        var counter: TimeModel.CountDown = TimeModel.CountDown(3_600_000_000, 1_000)
        counter.start()

        counter.isEnded.observe(lifecycleOwner) {
            if (it) {
                counter = TimeModel.CountDown(3_600_000_000, 1_000)
                counter.start()
            }
        }

        counter.leftInMillis.observe(lifecycleOwner) {
            val connectivityManager =
                hostActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            _isConnected.value = netInfo?.isConnected ?: false
        }

        _isConnected.observe(lifecycleOwner) {
            val hasChanged = when (getSharedPreference(SP_BOOL_IS_CONNECTED) as Boolean?) {
                true -> it != true //We register a change in state iff we were previously offline
                false -> true //We always register a change in state if there is no internet
                null -> {
                    editSharedPreference(SP_BOOL_IS_CONNECTED, it)
                    it != true
                }
            }

            if (hasChanged) {
                editSharedPreference(SP_BOOL_IS_CONNECTED, it)
                //We then update the network status
                headerBinding.textIsConnected.text =
                    if (it) hostActivity.getString(R.string.you_are_online)
                    else hostActivity.getString(R.string.you_are_offline)

                headerBinding.imgIsConnected.setImageResource(
                    if (it) R.drawable.outline_cloud_done_24
                    else R.drawable.outline_cloud_off_24
                )
                headerBinding.imgIsConnected.setColorFilter(
                    ContextCompat.getColor(
                        hostActivity,
                        if (it) R.color.colorPositiveButton
                        else R.color.colorNegativeButton
                    )
                )
                headerBinding.root.visibility = View.VISIBLE
            } else {//To make the "you are online" text disappear
                headerBinding.root.visibility =
                    View.GONE
            }
        }
    }

    /**
     * @since 12-01-2022
     *
     * To cache some data for future use
     * @param key must be from [SharedPKeys]
     * @return TRUE if correctly saved else FALSE
     */
    fun editSharedPreference(key: String, data: Any): Boolean {
        val sp = hostActivity.getSharedPreferences(
            "cache",
            Context.MODE_PRIVATE
        ).edit()
        val hasSaved = when (data) {
            is String -> {
                sp.putString(key, data)
                true
            }
            is Int -> {
                sp.putInt(key, data)
                true
            }
            is Long -> {
                sp.putLong(key, data)
                true
            }
            is Double -> {
                sp.putFloat(key, data.toFloat())
                true
            }
            is Float -> {
                sp.putFloat(key, data)
                true
            }
            is Boolean -> {
                sp.putBoolean(key, data)
                true
            }
            else -> {
                false
            }
        }
        sp.apply()
        return hasSaved
    }

    /**
     * @since 12-01-2022
     *
     * To retrieve data saved previously
     * @param key must be from [SharedPKeys]
     * @return Type Any else null if not found
     */
    fun getSharedPreference(key: String): Any? {
        val sp = hostActivity.getSharedPreferences(
            "cache",
            Context.MODE_PRIVATE
        )
        return sp.all[key]
    }

    companion object SharedPKeys {
        const val SP_BOOL_IS_CONNECTED = "BOOKER_IS_CONNECTED"
        const val SP_STRING_BOOKER_NAME = "BOOKER_NAME"
        const val SP_STRING_BOOKER_PHONE = "BOOKER_PHONE_NUMBER"
        const val SP_INT_BOOKER_COUNTRY_CODE = "BOOKER_COUNTRY_CODE"
        const val SP_BOOL_BOOKER_NEW = "BOOKER_IS_NEW"
        const val SP_BOOL_BOOKER_PROFILE_EXIST = "BOOKER_HAS_PROFILE"

        /**
         * @since 12-01-2022
         *
         * It handles loading spinners especially for fragments. It observes a livedata for loading and reacts accordingly
         * @param isBlocking if true, we make activity un clickable
         * @param customProgressBar is for fragments which have a specific progress bar e.g in a button or card etc
         */
        @JvmOverloads
        fun LiveData<Boolean>.handleProgress(
            uiUtils: UIUtils,
            isBlocking: Boolean = false,
            customProgressBar: ProgressBar? = null,
        ) {
//            val progressBar =
//                uiUtils.hostActivity.findViewById<ProgressBar>(R.id.progressBar)
//            this.observe(uiUtils.lifecycleOwner) {
//                if (it) {
//                    progressBar.visibility = View.VISIBLE
//                    if (isBlocking) {
//                        uiUtils.hostActivity.window.setFlags(
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        )
//                    }
//                } else {
//                    progressBar.visibility = View.GONE
//                    if (isBlocking) {
//                        uiUtils.hostActivity.window.clearFlags(
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        )
//                    }
//                }

//            }
        }
    }
}