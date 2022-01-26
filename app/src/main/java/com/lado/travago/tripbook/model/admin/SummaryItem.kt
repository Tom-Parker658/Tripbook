package com.lado.travago.tripbook.model.admin

import android.content.res.Resources
import com.google.firebase.firestore.DocumentSnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

data class SummaryItem(
    val id: String,
    val mainTitle: String,
    val subTitle: String,
    val logoResourceID: Int?,
    val state: SettingsItemState?,
    val isMainItem: Boolean = false,
    val logoUrl: String?,
    val extraDetails: String? = null,
    val isVisible: Boolean = true,
) {

    enum class SettingsItemState { OK, NOT_OK, PENDING }

    @ExperimentalCoroutinesApi
    companion object {
        const val ITEM_AGENCY_PROFILE_ID = "1"
        const val ITEM_SCAN_BOOKS_ID = "2"
        const val ITEM_AGENCY_TRIPS_ID = "3"
        const val ITEM_SCANNERS_ID = "4"
        const val ITEM_TAKE_OFF_TIME_ID = "5"
        const val ITEM_PLAN_EVENTS_ID = "6"
        const val ITEM_MONEY_ID = "7"

        @JvmStatic
        fun adminAgencyConfigOptions(
            resources: Resources,
            scannerDoc: DocumentSnapshot,
            agencyDoc: DocumentSnapshot,
        ): List<SummaryItem> {
            val isAdmin = scannerDoc.getBoolean("isAdmin")!!
            return listOf(
                SummaryItem(
                    ITEM_AGENCY_PROFILE_ID,
                    resources.getString(R.string.text_agency_profile),
                    subTitle = if (isAdmin) "Tell more about the agency & customize it" else "See the details of your agency",
                    logoResourceID = R.drawable.outline_agency_profile_24,
                    isMainItem = true,
                    state = SettingsItemState.OK,
                    logoUrl = agencyDoc.getString("logoUrl")
                ),
                SummaryItem(
                    ITEM_SCAN_BOOKS_ID,
                    resources.getString(R.string.text_all_trip_books),
                    "Scan Booker's Books",
                    R.drawable.baseline_qr_code_scanner_24,
                    state = if (agencyDoc.getBoolean("hasScans")!!) SettingsItemState.OK else SettingsItemState.NOT_OK,
                    logoUrl = null
                ),
                SummaryItem(
                    ITEM_AGENCY_TRIPS_ID,
                    "${resources.getString(R.string.text_towns)} & ${resources.getString(R.string.text_trips)}",
                    if (isAdmin) "Add or remove Destination,Localities & Parks" else "See all Destinations,Localities & Parks",
                    R.drawable.baseline_directions_bus_24,
                    state = if (agencyDoc.getBoolean("hasTrips")!!) SettingsItemState.OK else SettingsItemState.NOT_OK,
                    logoUrl = null
                ),

                SummaryItem(
                    ITEM_SCANNERS_ID,
                    if (isAdmin) resources.getString(R.string.text_scanners) else resources.getString(
                        R.string.text_scanner),
                    if (isAdmin) "Add, remove or promote your Scanners" else "See your Scanner profile",
                    if (isAdmin) R.drawable.baseline_groups_24 else R.drawable.baseline_account_box_24,
                    state = if (agencyDoc.getBoolean("hasScanners")!!) SettingsItemState.OK else SettingsItemState.NOT_OK,
                    logoUrl = if (!isAdmin) scannerDoc.getString("photoUrl") else null
                ),

                SummaryItem(
                    ITEM_TAKE_OFF_TIME_ID,
                    resources.getString(R.string.text_take_off_periods),
                    subTitle = if (isAdmin) "Schedule the take-off periods for the day" else "See take-off periods for the day",
                    R.drawable.baseline_departure_board_24,
                    state = if (agencyDoc.getBoolean("hasTakeOffPeriods")!!) SettingsItemState.OK else SettingsItemState.NOT_OK,
                    logoUrl = null
                ),

                SummaryItem(
                    ITEM_PLAN_EVENTS_ID,
                    resources.getString(R.string.text_event_planner),
                    if (isAdmin) "Plan upcoming events for you agency" else "See upcoming planned events",
                    R.drawable.baseline_calendar_today_24,
                    state = if (agencyDoc.getBoolean("hasOngoingEvent")!!) SettingsItemState.PENDING else SettingsItemState.OK,
                    logoUrl = null
                ),
                SummaryItem(
                    ITEM_MONEY_ID,
                    resources.getString(R.string.text_money),
                    if (isAdmin) "Manage payment methods & Discounts" else "My Income Stats",
                    R.drawable.baseline_payments_24,
                    state = if (agencyDoc.getBoolean("hasConfiguresPayments")!!) SettingsItemState.OK else SettingsItemState.NOT_OK,
                    logoUrl = null
                ),
                SummaryItem(
                    "0",
                    resources.getString(R.string.text_money),
                    "",
                    0,
                    SettingsItemState.OK,
                    logoUrl = null,
                    isVisible = false
                ),

                )
        }


        fun createSummaryItemsFromBooks(
            snapshots: List<DocumentSnapshot>,
        ): List<SummaryItem> {
            val books = mutableListOf<SummaryItem>()
            snapshots.forEach {
                val tripTime = TimeModel.fromTimeParameter(
                    TimeModel.TimeParameter.MILLISECONDS,
                    it.getLong("tripTimeInMillis")!!
                )
                val datePlusTime = "${
                    Utils.formatDate(
                        it.getLong("travelDateMillis")!!,
                        "EEEE dd MM yyyy"
                    )
                }, ${tripTime.formattedTime(TimeModel.TimeFormat.FORMAT_24H)}"

                //It is a main item if it is happening today
                val numberOfDaysFromNowToTakeOff = Utils.getNumberOfDaysBetween(
                    it.getLong("travelDateMillis")!!,
                    Calendar.getInstance().timeInMillis
                )
                var state: SettingsItemState? = null
                var extra: String? = null
                when {
                    it.getBoolean("taken")!! -> {
                        state = SettingsItemState.OK
                        extra = "Done"
                    }
                    it.getBoolean("taken")!! && it.getBoolean("isExpired")!! -> {
                        state = SettingsItemState.NOT_OK
                        extra = "Missed"
                    }
                    //If we are still to take the trip, it is state pending
                    Date().before(Date(it.getLong("travelDateMillis")!!)) -> state =
                        SettingsItemState.PENDING
                }


                books += SummaryItem(
                    id = it.getString("failed")!!,//We get the qrCode string
                    mainTitle = "${it.getString("tripLocalityName")!!} -> ${it.getString("tripDestinationName")!!}",
                    subTitle = datePlusTime,
                    logoResourceID = null,
                    state = state,
                    isMainItem = numberOfDaysFromNowToTakeOff <= 1.0,
                    logoUrl = it.getString("agencyLogoUrl") ?: "",//TODO: Remove this
                    extraDetails = extra
                )
            }
            return books
        }

        const val ITEM_BOOKER_PROFILE_ID = "1"
        const val ITEM_SWAP_PHONE_ID = "2"
        fun createForBookerConfigOptions(resources: Resources) = listOf(
            SummaryItem(
                ITEM_BOOKER_PROFILE_ID,
                resources.getString(R.string.text_booker_profile),
                subTitle = "OK!",
                logoResourceID = R.drawable.baseline_person_24,
                isMainItem = true,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                ITEM_SWAP_PHONE_ID,
                resources.getString(R.string.text_change_phone_number),
                subTitle = "OK!",
                logoResourceID = R.drawable.baseline_phone_24,
                isMainItem = false,
                state = null,
                logoUrl = null
            ),
        )
    }
}

