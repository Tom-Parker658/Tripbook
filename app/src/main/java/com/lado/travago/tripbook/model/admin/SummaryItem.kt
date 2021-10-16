package com.lado.travago.tripbook.model.admin

import android.content.res.Resources
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.utils.Utils
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

    companion object {
        @JvmStatic
        fun getAdminScannerItems(resources: Resources) = listOf(
            SummaryItem(
                "1",
                resources.getString(R.string.text_label_agency_config_profile),
                subTitle = "OK!",
                logoResourceID = R.drawable.baseline_password_24,
                isMainItem = true,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "2",
                resources.getString(R.string.text_label_agency_config_intervals),
                "OK!",
                R.drawable.baseline_departure_board_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "3",
                resources.getString(R.string.text_label_agency_config_trips),
                "OK!",
                R.drawable.baseline_directions_bus_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "4",
                resources.getString(R.string.text_label_agency_config_scanners),
                "OK!",
                R.drawable.baseline_groups_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "5",
                resources.getString(R.string.text_label_agency_config_events_planner),
                "OK!",
                R.drawable.baseline_calendar_today_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "6",
                resources.getString(R.string.text_label_agency_config_money),
                "OK!",
                R.drawable.baseline_payments_24,
                state = null,
                logoUrl = null
            ),
            SummaryItem(
                "7",
                resources.getString(R.string.text_label_agency_config_delete_agency),
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
                val tripTime = TimeModel.from24Format(
                    it.getLong("tripHour")!!.toInt(),
                    it.getLong("tripMinutes")!!.toInt(),
                )
                val datePlusTime = "${
                    Utils.formatDate(
                        it.getLong("tripDateInMillis")!!,
                        "EEEE dd/mm/yyyy"
                    )
                } ,${tripTime.formattedTime(TimeModel.TimeFormat.FORMAT_24H)}"

                //It is a main item if it is happening today
                val numberOfDaysFromNowToTakeOff = Utils.getNumberOfDaysBetween(
                    it.getLong("tripDateInMillis")!!,
                    Calendar.getInstance().timeInMillis
                )
                var state: SettingsItemState? = null
                var extra: String? = null
                when {
                    it.getBoolean("tripWasDone")!! -> {
                        state = SettingsItemState.OK
                        extra = "Done"
                    }
                    it.getBoolean("tripWasDone")!! && it.getBoolean("isExpired")!! -> {
                        state = SettingsItemState.NOT_OK
                        extra = "Missed"
                    }
                    //If we are still to take the trip, it is state pending
                    Date().before(Date(it.getLong("tripDateInMillis")!!)) -> state =
                        SettingsItemState.PENDING
                }


                books += SummaryItem(
                    id = it.id,
                    mainTitle = "${it.getString("locality")!!} -> ${it.getString("destination")!!}",
                    subTitle = datePlusTime,
                    logoResourceID = null,
                    state = state,
                    isMainItem = numberOfDaysFromNowToTakeOff <= 1.0,
                    logoUrl = it.getString("agencyLogoUrl"),
                    extraDetails = extra
                )
            }
            return books
        }
    }
}

