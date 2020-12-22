package com.lado.travago.transpido.viewmodel.panel

import androidx.lifecycle.ViewModel
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import com.lado.travago.transpido.repo.firebase.StorageRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModel to manage all bus related fragments.
 */
@ExperimentalCoroutinesApi
class BusesViewModel: ViewModel() {
    private val dbRepo = FirestoreRepo()
    private val storageRepo = StorageRepo()



}