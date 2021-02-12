package com.lado.travago.tripbook.ui.users

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.ActivityUserLoginBinding
import com.lado.travago.tripbook.model.users.Scanner
import com.lado.travago.tripbook.utils.contracts.UserCreationContract
import com.lado.travago.tripbook.viewmodel.admin.UserLoginViewModel
import kotlinx.coroutines.*

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class UserLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserLoginBinding
    private lateinit var viewModel: UserLoginViewModel
    private lateinit var userCreationLauncher: ActivityResultLauncher<Pair<String?, String?>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userCreationLauncher = registerForActivityResult(UserCreationContract()){}
        viewModel = UserLoginViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_login)
        clickListeners()
        showProgress()
        navigateToLauncherUI()
        onFieldChange()
        restoreFields()
    }

    private fun showProgress() = viewModel.loading.observe(this){
        if(it) binding.loginProgress.visibility = View.VISIBLE
        else binding.loginProgress.visibility = View.GONE
    }

    /**
     * Set ways to get data from the views and assign it to the viewModels
     */
    private fun onFieldChange() {
        binding.email.editText!!.addTextChangedListener {
            viewModel.setField(
                binding.email.editText!!.text.toString(),
                UserLoginViewModel.LoginFieldTags.EMAIL,
            )
        }
        binding.password.editText!!.addTextChangedListener {
            viewModel.setField(
                binding.password.editText!!.text.toString(),
                UserLoginViewModel.LoginFieldTags.PASSWORD,
            )
        }
    }
    private fun restoreFields(){
        binding.email.editText!!.setText(viewModel.email)
        binding.password.editText!!.setText(viewModel.password)
    }

    /**
     * Navigates back to the UI which originally launched this creation as an intent. It returns with no data as intent,
     * But only with the RESULT status
     */
    private fun navigateToLauncherUI() =
        viewModel.userLoggedIn.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK, null)
                finish()
            }
        }
    private fun clickListeners(){
        binding.btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.loginUser()
            }
        }
        binding.btnCreateAccount.setOnClickListener {
            /**
             * A contract call to launch the scannerCreationActivity and return the result which is a [Scanner.ScannerBasicInfo] object.
             * This object is added to the list of scanners from the view model
             * @see Scanner.ScannerBasicInfo
             * @see UserCreationContract
             */
            userCreationLauncher.launch(Pair(null, null), null)
        }
        binding.btnForgotPassword.setOnClickListener {
            //TODO Send Verification message
        }
    }

}