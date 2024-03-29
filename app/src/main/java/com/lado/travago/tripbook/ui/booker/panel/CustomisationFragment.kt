package com.lado.travago.tripbook.ui.booker.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lado.travago.tripbook.R

/**
 * Seat selection and bus customisation fragment
 */

class CustomisationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customisation, container, false)
    }

}