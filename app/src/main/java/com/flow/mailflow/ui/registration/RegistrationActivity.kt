package com.flow.mailflow.ui.registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.databinding.ActivityRegistrationBinding
import com.flow.mailflow.ui.home.HomeActivity
import com.flow.mailflow.utils.SharedPreferenceHelper
import timber.log.Timber

class RegistrationActivity : BaseActivity() {

    private val vm: RegistrationViewModel by viewModels()
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registerButton.setOnClickListener {
            if (onValidate()) {/*startActivity(Intent(this,HomeActivity::class.java))
                finishAffinity()*/
                registerApi()
            }

        }
    }

    private fun registerApi() {

        val registerRequest =RegisterRequest(
            name = binding.fullNameEditText.text.toString(),
            username = binding.usernameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
        )

        vm.register(
            registerRequest
        ).observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    // showLoading(false)
                }

                Status.ERROR -> {
                    toastError(this, it.response?.message ?: it.message)
                }

                Status.SUCCESS -> {
                    if (it.response?.errorcode == 0) {
                        Timber.tag("SuccessFromRegister").e(it.response.toString())
                        loginApi()
                    }else{
                        toastError(this, it.response?.message ?: it.message)
                    }

                }
            }
        }

    }

    private fun loginApi() {
        val loginRequest =LoginRequest(
            username = binding.usernameEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
        )
        vm.login(
            loginRequest
        ).observe(this) { its ->
            when (its.status) {
                Status.LOADING -> {
                    //showLoading(true)
                }

                Status.COMPLETED -> {
                    showLoading(false)
                }

                Status.ERROR -> {
                    toastError(this, its.response?.message ?: its.message)
                }

                Status.SUCCESS -> {
                    /*val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                    sharedPreferences.edit().putString("token", its.response?.data?.apiKey).apply()*/
                    SharedPreferenceHelper.with(this).putString(SharedPreferenceHelper.TOKEN,its.response?.data?.apiKey)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finishAffinity()
                }
            }


        }
    }

    private fun onValidate(): Boolean {
        var status = true
        if (binding.fullNameEditText.text.toString().isEmpty()) {
            status = false
            binding.fullNameEditText.error = "Required"
        }
        if (binding.emailEditText.text.toString().isEmpty()) {
            status = false
            binding.emailEditText.error = "Required"
        }
        if (binding.usernameEditText.text.toString().isEmpty()) {
            status = false
            binding.usernameEditText.error = "Required"
        }
        if (binding.passwordEditText.text.toString().isEmpty()) {
            status = false
            binding.passwordEditText.error = "Required"
        }
        return status

    }


}