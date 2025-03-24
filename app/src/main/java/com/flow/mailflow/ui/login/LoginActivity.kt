package com.flow.mailflow.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.databinding.ActivityLoginBinding
import com.flow.mailflow.repo.prefs
import com.flow.mailflow.ui.home.HomeActivity
import com.flow.mailflow.ui.registration.RegistrationActivity
import com.flow.mailflow.ui.registration.RegistrationViewModel
import com.flow.mailflow.utils.SharedPreferenceHelper
import com.flow.mailflow.utils.Utils.timberCall

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val vm: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)

        binding.loginButton.setOnClickListener {
            if (onValidate()) {
                loginApi()
            }

            //startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun loginApi() {
        val loginRequest = LoginRequest(
            username = binding.usernameEditText.text.toString(),
            password = binding.passwordEditText.text.toString(),
        )
        vm.login(
            loginRequest
        ).observe(this) { its ->
            when (its.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    showLoading(false)
                }

                Status.ERROR -> {
                    toastError(this, its.response?.message ?: its.message)
                }

                Status.SUCCESS -> {

                    val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                    sharedPreferences.edit().putString("token", its.response?.data?.apiKey).apply()

                    timberCall(this,"tokenGetFromLogin",sharedPreferences.getString(SharedPreferenceHelper.TOKEN,"")?:"Not found")
                    startActivity(Intent(this, HomeActivity::class.java))
                    finishAffinity()
                }
            }


        }
    }

    private fun onValidate(): Boolean {
        var status = true
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
