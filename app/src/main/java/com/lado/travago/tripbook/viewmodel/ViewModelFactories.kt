@file:Suppress("UNCHECKED_CAST")

package com.lado.travago.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.viewmodel.admin.ScannerCreationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * A factory file to build custom viewModels with arguments
 */

///**
//     * This is a factory class which will be used to build our custom [JourneySearchViewModel] with arguments
//     * @property placesClient Needed by [JourneySearchViewModel] to compute Place related operations.
//     */
//@ExperimentalCoroutinesApi
//@InternalCoroutinesApi
//class SearchJourneyViewModelFactory(
//    private val application: Application,
//    private val placesClient: PlacesClient,
//) : ViewModelProvider.Factory {
//    /**
//     * Creates a new instance of the given `Class`.
//     * @param modelClass a `Class` whose instance is requested
//     * @param <T>        The type parameter for the ViewModel.
//     * @return a newly created ViewModel
//    </T> */
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(JourneySearchViewModel::class.java)) {
//            return JourneySearchViewModel(application = application, placesClient = placesClient) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

/**
 * This is a factory class which will be used to build our custom [ScannerCreationViewModel] with arguments
 * @property agencyName is teh name of the agency which will be filled to the agencyName field of the
 * creation form
 * @property agencyFirestorePath is the path of the agency Document stored in firestore. This will be used
 * during the scanner creation
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ScannerCreationViewModelFactory(
    private val agencyName: String,
    private val agencyFirestorePath: String,
) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     * @param modelClass a `Class` whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return a newly created ViewModel
    </T> */

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerCreationViewModel::class.java))
            return  ScannerCreationViewModel(agencyFirestorePath, agencyName) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
