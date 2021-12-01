package com.lado.travago.tripbook.ui.agency.scanner_panel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityScannerPanelBinding
import com.lado.travago.tripbook.ui.agency.scanner_panel.viewmodel.ScannerPanelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ScannerPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerPanelBinding
    private lateinit var viewModel: ScannerPanelViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //We only inflate the layout when the current user is a scanner
        viewModel = ViewModelProvider(this)[ScannerPanelViewModel::class.java]

        if (viewModel.bookerDoc.value == null) {
            CoroutineScope(Dispatchers.Main).launch { viewModel.getCurrentScanner() }
            viewModel.bookerDoc.observe(this) {
                if (it.exists()) {
                    binding = DataBindingUtil.setContentView(this, R.layout.activity_scanner_panel)
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_scanner_panel)
        }
    }


}