package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookScanningBinding
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BookScanningViewModel
import com.lado.travago.tripbook.utils.contracts.QRScanContract
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
    @ExperimentalCoroutinesApi
class FragmentBookScanning: Fragment(){
    private lateinit var binding: FragmentBookScanningBinding
    private lateinit var viewModel: BookScanningViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_book_scanning, container, false)
        onClickBtnScan()
        return binding.root
    }

    /**Start and return the result from the scan intent*/
    private val qrCodeScanIntent = registerForActivityResult(QRScanContract()){ content ->
        Toast.makeText(requireContext(), content, Toast.LENGTH_LONG).show()
        Log.i("QR", content)
    }

    /**
     * Launches the scan fragment
     */
    private fun onClickBtnScan() = binding.btnScanQr.setOnClickListener {
        qrCodeScanIntent.launch(this)
    }

}