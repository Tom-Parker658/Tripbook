package com.lado.travago.tripbook.ui.scanner.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lado.travago.tripbook.R


/**
 * A simple [Fragment] subclass.
 * Use the [AgencyStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgencyStatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agency_stats, container, false)
    }


}