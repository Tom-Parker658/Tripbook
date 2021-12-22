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
    val extraDetails: String? = null
) {

    enum class SettingsItemState { OK, NOT_OK, PENDING }

    @ExperimentalCoroutinesApi
    companion object {
        @JvmStatic
        fun createForAgencyConfigOptions(resources: Resources) = listOf(
            SummaryItem(
                "1",
                resources.getString(R.string.text_agency_details),
                subTitle = "OK!",
                logoResourceID = R.drawable.outline_agency_profile_24,
                isMainItem = true,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "2",
                resources.getString(R.string.text_take_off_periods),
                "OK!",
                R.drawable.baseline_departure_board_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "3",
                resources.getString(R.string.text_trips),
                "OK!",
                R.drawable.baseline_directions_bus_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "4",
                resources.getString(R.string.text_scanners),
                "OK!",
                R.drawable.baseline_groups_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "5",
                resources.getString(R.string.text_event_planner),
                "OK!",
                R.drawable.baseline_calendar_today_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "6",
                resources.getString(R.string.text_money),
                "OK!",
                R.drawable.baseline_payments_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "7",
                resources.getString(R.string.text_delete_agency),
                "OK!",
                R.drawable.round_cancel_24,
                state = null,
                logoUrl = null
            )
        )

        fun createSummaryItemsFromBooks(
            snapshots: List<DocumentSnapshot>
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

        fun createForBookerConfigOptions(resources: Resources) = listOf(
            SummaryItem(
                "1",
                resources.getString(R.string.text_booker_profile),
                subTitle = "OK!",
                logoResourceID = R.drawable.baseline_person_24,
                isMainItem = true,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "2",
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

