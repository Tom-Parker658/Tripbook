package com.lado.travago.tripbook.model.admin

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

interface AsyncTaskRunnable {
    val hostActivity: Activity?
    val hostFragment: Fragment?
    val hostViewModel: ViewModel



}