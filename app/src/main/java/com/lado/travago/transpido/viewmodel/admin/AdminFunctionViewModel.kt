package com.lado.travago.transpido.viewmodel.admin

import androidx.lifecycle.ViewModel
import com.lado.travago.transpido.model.enums.DataResources
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class AdminFunctionViewModel: ViewModel() {
    private val dbRepo = FirestoreRepo()

    fun upload(){
        val journeyList = DataResources.journeyDistanceList.trimIndent().reader().buffered().readLines()
        for(journey in journeyList){
            val destinationToJourney = journey.split(" ").toMutableList()
            destinationToJourney.removeAt(2)
            val locationName = destinationToJourney[0]
            val destinationName = destinationToJourney[1]
        }
    }

}