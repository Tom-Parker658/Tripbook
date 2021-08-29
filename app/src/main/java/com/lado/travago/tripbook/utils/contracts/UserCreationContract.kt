package com.lado.travago.tripbook.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.lado.travago.tripbook.ui.agency.creation.AgencyCreationFragment
//import com.lado.travago.tripbook.ui.agency.creation.config_panel.AgencyConfigActivity.AgencyConfigResources.StartUpTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * This is a contract to launch the configuration activity for an agency
 * Uses the [StartUpTags] to specify what we actually want to do
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AgencyConfigContract : ActivityResultContract<Bundle, String>() {

    /** Create an intent that can be used for [Activity.startActivityForResult]  */
    override fun createIntent(
        context: Context,
        dataBundle: Bundle
    ) = Intent(context, AgencyCreationFragment::class.java)

    /** Convert result obtained from [Activity.onActivityResult] to Output  */
    override fun parseResult(resultCode: Int, infoIntent: Intent?) = when (resultCode) {
        Activity.RESULT_OK -> "OK"
        else -> ""
    }

}