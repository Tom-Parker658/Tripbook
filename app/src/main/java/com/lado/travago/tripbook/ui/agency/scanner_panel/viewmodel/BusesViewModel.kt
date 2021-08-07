package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import com.lado.travago.tripbook.repo.firebase.StorageRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModel to manage all bus related fragments.
 */
@ExperimentalCoroutinesApi
class BusesViewModel: ViewModel() {
    private val dbRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()



}