package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusOverviewViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [BusesToFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BusesToFragment : Fragment() {
    private lateinit var viewModel: BusOverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buses_to, container, false)
    }

}