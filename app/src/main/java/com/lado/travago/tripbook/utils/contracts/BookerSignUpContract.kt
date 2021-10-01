package com.lado.travago.tripbook.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationActivity
//import com.lado.travago.tripbook.ui.agency.config_panel.AgencyConfigActivity.AgencyConfigResources.StartUpTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * This is a contract to launch the logIn or creation for a booker
 * Uses the [StartUpTags] to specify what we actually want to do
 */
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class BookerSignUpContract : ActivityResultContract<Bundle, Int>() {

    /** Create an intent that can be used for [Activity.startActivityForResult]  */
    override fun createIntent(
        context: Context,
        dataBundle: Bundle
    ) = Intent(context, BookerCreationActivity::class.java)

    /** Convert result obtained from [Activity.onActivityResult] to Output  */
    override fun parseResult(resultCode: Int, infoIntent: Intent?) = resultCode

}