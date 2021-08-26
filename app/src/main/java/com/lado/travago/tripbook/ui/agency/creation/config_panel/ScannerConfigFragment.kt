package com.lado.travago.tripbook.ui.agency.creation.config_panel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentScannerConfigBinding


/**
 * Here the agency administrators can add other scanners
 */
class ScannerConfigFragment : Fragment() {
    private lateinit var binding: FragmentScannerConfigBinding
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner_config, container, false)
    }

}