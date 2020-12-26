package com.lado.travago.tripbook.utils.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator

class QRScanContract: ActivityResultContract<Fragment, String>() {
    /**
     * Creates the scan intent using a fragment
     */
    override fun createIntent(context: Context, fragment: Fragment): Intent =
        IntentIntegrator.forSupportFragment(fragment)
            //Specifies only QR CODES and not bar codes should be scanned
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setBeepEnabled(true)//Enables the beep sound during scanning
            .setOrientationLocked(false)//Unlocks phone orientation during scanning
            .setPrompt("Scan QR Ticket!")// Text to be displayed to tell the admin to scan
            .setTorchEnabled(true)//Enables the torch on the camera to make it more visible
            .createScanIntent()

    /**
     * Parses the result from the scan and returns the content of the string or an empty string
     */
    override fun parseResult(resultCode: Int, scanIntent: Intent?): String {
        return when(resultCode){
            Activity.RESULT_OK -> {
                IntentIntegrator.parseActivityResult(resultCode, scanIntent).contents
            }
            else -> "No information"
        }
    }
}