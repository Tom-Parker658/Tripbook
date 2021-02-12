package com.lado.travago.tripbook.utils.contracts

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * A way to make the app navigates back to the caller of th logIn
 */
class UserLoginContract: ActivityResultContract<String?, String?>() {
    /** Create an intent that can be used for [Activity.startActivityForResult]  */
    override fun createIntent(context: Context, input: String?) = Intent()

    /** Convert result obtained from [Activity.onActivityResult] to O  */
    override fun parseResult(resultCode: Int, intent: Intent?) = ""
}