package com.lado.travago.tripbook.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.lado.travago.tripbook.model.enums.OCCUPATION
import com.lado.travago.tripbook.model.users.User
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationActivity
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * This is a contract to launch the scanner creation from the agency registration
 */
@Suppress("KDocUnresolvedReference")
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class UserCreationContract: ActivityResultContract<Pair<String?, String?>, User.UserBasicInfo>() {

    /** Create an intent that can be used for [Activity.startActivityForResult]  */
    override fun createIntent(context: Context, agencyNameToDBPathPair: Pair<String?, String?>): Intent {
        val agencyName = agencyNameToDBPathPair.first
        val agencyDBPath = agencyNameToDBPathPair.second
        return Intent(context,  BookerCreationActivity::class.java)
            .putExtra(AgencyCreationActivity.KEY_AGENCY_NAME, agencyName)
            .putExtra(AgencyCreationActivity.KEY_OTA_PATH, agencyDBPath)
    }

    /** Convert result obtained from [Activity.onActivityResult] to Output  */
    override fun parseResult(resultCode: Int, infoIntent: Intent?) = when(resultCode){
        Activity.RESULT_OK -> {
            User.UserBasicInfo(
                name = infoIntent?.getStringExtra(AgencyCreationActivity.KEY_SCANNER_NAME) ?: "name",
                phoneNumber = infoIntent?.getStringExtra(AgencyCreationActivity.KEY_SCANNER_PHONE) ?: "655223344",
                photoUrl = infoIntent?.getStringExtra(AgencyCreationActivity.KEY_SCANNER_URL) ?: "ss",
                birthdayInMillis = infoIntent?.getLongExtra(AgencyCreationActivity.KEY_SCANNER_BIRTHDAY, Date().time) ?: 0L,
                occupation = OCCUPATION.SCANNER
            )
        }
        else -> User.UserBasicInfo(
            "",
            Date().time,
            "",
            "",
            OCCUPATION.SCANNER
        )
    }
}