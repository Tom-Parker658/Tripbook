@file:Suppress("UNCHECKED_CAST")

package com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.model.users.Booker
import com.lado.travago.tripbook.model.users.Scanner
import com.lado.travago.tripbook.ui.booker.creation.BookerCreationViewModel
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
//class BookerCreationViewModelFactory(
//    private val context: Context
//) : ViewModelProvider.Factory {
//    /**
//     * Creates a new instance of the given `Class`.
//     * @param modelClass a `Class` whose instance is requested
//     * @param <T>        The type parameter for the ViewModel.
//     * @return a newly created ViewModel
//    </T> */
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(BookerCreationViewModel::class.java)) {
//            return BookerCreationViewModel(context = context) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}bui

/**
 * This is a factory class which will be used to build our custom [BookerCreationViewModel] with arguments
 * For this creation to be
 * -[Scanner] Creation: All the properties must not be null
 * @property agencyName is the name of the Agemcy which employed  this scanner
 * @property agencyFirestorePath is the path of the agency Document stored in firestore. This will be used
 * during the scanner creation
 * @property agencyId is the id of the agency

 * -[Booker] Creation: All the properties must be null

 */
