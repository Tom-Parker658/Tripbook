package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import com.journeyapps.barcodescanner.Decoder
import com.journeyapps.barcodescanner.DecoderFactory
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookScanningBinding
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.BusesManageViewModel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@ExperimentalCoroutinesApi
class BookScanningFragment : Fragment() {
    private lateinit var binding: FragmentBookScanningBinding
    private lateinit var viewModel: BusesManageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_book_scanning,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity())[BusesManageViewModel::class.java]
        fabListeners()
        continuousScanner()
        observeLiveData()

        return binding.root
    }

    private fun fabListeners() {
        binding.fabToggleTorch.setOnClickListener {
            ///Just to switch on and off torch,NB: the value parameter does not matter
            viewModel.setField(FieldTags.TORCH_STATE, true)
            if (viewModel.isTorchOn) binding.qrCodeView.setTorchOn()
            else binding.qrCodeView.setTorchOff()
        }
    }

    override fun onResume() {
        binding.qrCodeView.decoderFactory = DecoderFactory {
            Decoder(QRCodeMultiReader())
        }
        binding.qrCodeView.resume()
        super.onResume()
    }

    override fun onStop() {
        binding.qrCodeView.pause()
        super.onStop()
    }

    fun observeLiveData() {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it != null && it.isNotBlank()) {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }

        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.updateProgressBar.visibility = View.VISIBLE
                //Makes the screen untouchable
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                binding.updateProgressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        viewModel.pauseScanView.observe(viewLifecycleOwner) {
            //If true, we upload changes to db
            if (it) {
                binding.qrCodeView.pause()
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.onCodeScanned(
                        //We better crash the program for now
                        //TODO: Display a messaage
                        viewModel.authRepo.currentUser!!.uid
                    )
                }
            } else {
                binding.qrCodeView.resume()
            }
        }
        //Up to date data
        viewModel.destinationOverviews.observe(viewLifecycleOwner) {
            viewModel.getLatestData(it)
        }

    }

    /**Start and return the result from the scan intent*/
    private fun continuousScanner() {
        binding.qrCodeView.decodeContinuous {
            /*
            *once we scan a code, we pause until we get the response to updating the database and
            *then we can display required response
            */
            viewModel.setField(
                FieldTags.SCAN_RESULT,
                it
            )
            Log.i("QR_CODE", it.text)
            viewModel.setField(FieldTags.PAUSE_SCAN_VIEW, true)

        }
    }

}