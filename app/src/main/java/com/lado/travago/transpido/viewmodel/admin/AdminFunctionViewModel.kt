package com.lado.travago.transpido.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.places.PlacesRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AdminFunctionViewModel: ViewModel() {
    private val placeRepo = PlacesRepo(null)

    suspend fun upload(){
        placeRepo.addJourneys().collect {
            when(it){
                is State.Failed -> {
                    Log.e("AdminJourneys", it.message)
                }
                is State.Success -> {
                    Log.i("AdminJourneys", it.toString())
                }
            }
        }
    }

}