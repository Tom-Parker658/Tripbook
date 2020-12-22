package com.lado.travago.transpido.ui.scanner.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lado.travago.transpido.R


/**
 * A simple [Fragment] subclass.
 * Use the [AgencyBusesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgencyBusesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agency_journey, container, false)
    }

}