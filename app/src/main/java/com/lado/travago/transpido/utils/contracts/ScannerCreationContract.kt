package com.lado.travago.transpido.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.lado.travago.transpido.model.admin.Scanner
import com.lado.travago.transpido.ui.agency.AgencyRegistrationActivity
import com.lado.travago.transpido.ui.scanner.creation.ScannerCreationActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

/**
 * This is a contract to launch the scanner creation from the agency registration
 */
@Suppress("KDocUnresolvedReference")
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerCreationContract: ActivityResultContract<Pair<String, String>, Scanner.ScannerBasicInfo>() {

    /** Create an intent that can be used for [Activity.startActivityForResult]  */
    override fun createIntent(context: Context, agencyNameToDBPathPair: Pair<String, String>): Intent {
        val agencyName = agencyNameToDBPathPair.first
        val agencyDBPath = agencyNameToDBPathPair.second
        return Intent(context,  ScannerCreationActivity::class.java)
            .putExtra(AgencyRegistrationActivity.KEY_AGENCY_NAME, agencyName)
            .putExtra(AgencyRegistrationActivity.KEY_OTA_PATH, agencyDBPath)
    }

    /** Convert result obtained from [Activity.onActivityResult] to Output  */
    override fun parseResult(resultCode: Int, infoIntent: Intent?) = when(resultCode){
        Activity.RESULT_OK -> {
            Scanner.ScannerBasicInfo(
                name = infoIntent?.getStringExtra(AgencyRegistrationActivity.KEY_SCANNER_NAME)!!,
                phoneNumber = infoIntent.getStringExtra(AgencyRegistrationActivity.KEY_SCANNER_PHONE)!!,
                photoUrl = infoIntent.getStringExtra(AgencyRegistrationActivity.KEY_SCANNER_URL)!!,
                isAdmin = infoIntent.getBooleanExtra(AgencyRegistrationActivity.KEY_SCANNER_IS_ADMIN, false),
                birthdayInMillis = infoIntent.getLongExtra(AgencyRegistrationActivity.KEY_SCANNER_BIRTHDAY, Date().time),
            )
        }
        else -> Scanner.ScannerBasicInfo(
            "",
            Date().time,
            false,
            "",
            ""
        )
    }
}