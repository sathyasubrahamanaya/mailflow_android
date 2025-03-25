package com.flow.mailflow.ui.confirm

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.response_data.sub_response.EmailContent
import com.flow.mailflow.data_models.response_data.sub_response.GenerateEmailResponse
import com.flow.mailflow.databinding.ActivityConfirmBinding
import com.flow.mailflow.ui.home.HomeViewModel
import com.flow.mailflow.utils.Utils.timberCall
import com.google.gson.Gson
import timber.log.Timber

class ConfirmActivity : BaseActivity() {
    lateinit var binding: ActivityConfirmBinding
    val recipient = "abin@webcastle.in"
    val ccRecipients = arrayOf("cc1@example.com", "cc2@example.com")
    val subject = "Your Subject Here"
    val body = "Your email body here."

    private val vm: HomeViewModel by viewModels()

    var mailResponse = GenerateEmailResponse()
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(binding.root)

        val json = intent.getStringExtra("emailContent")
        mailResponse = Gson().fromJson(json, GenerateEmailResponse::class.java)

        assignData(mailResponse.emailContent)

        binding.sendToGmailButton.setOnClickListener {
            if(validate()){
                sendEmail()
            }
        }

        binding.bottomSendButton.setOnClickListener {
            if(binding.bottomTextInput.text.toString().isEmpty()) {
                binding.bottomTextInput.error = "Required"
            }else{
                generateFileApi()
            }
        }

    }



    private fun generateFileApi() {
        vm.generateMail(
            null,
            binding.bottomTextInput.text.toString(),
            "",
            null
        ).observe(this){
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    showLoading(false)
                }

                Status.ERROR -> {
                    toastError(this, it.response?.message ?: it.message)
                }

                Status.SUCCESS -> {
                    if (it.response?.errorcode == 0) {
                        Timber.tag("SuccessFromHome").e(it.response.data?.emailContent?.toString())
                        assignData(it.response.data?.emailContent)
                    }else{
                        toastError(this, it.response?.message ?: it.message)
                    }
                }
            }
        }
    }

    private fun assignData(emailContent: EmailContent?) {
        binding.recipientEditText.setText(emailContent?.recipientEmail)
        binding.subjectEditText.setText(emailContent?.subject)
        binding.bodyEditText.setText(emailContent?.body)
    }

    private fun sendEmail() {
        timberCall(this, "Click", "onClick")
        val uriText =
            "mailto:${binding.recipientEditText.text}" + "?subject=" + Uri.encode(binding.subjectEditText.text.toString()) + "&body=" + Uri.encode(binding.bodyEditText.text.toString())
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriText))
        startActivity(emailIntent)
    }

    private fun validate(): Boolean {
        var status = true
        if (binding.recipientEditText.text.toString().isEmpty()) {
            status = false
            binding.recipientEditText.error = "Required"
        }
        if (binding.subjectEditText.text.toString().isEmpty()) {
            status = false
            binding.subjectEditText.error = "Required"
        }
        if (binding.bodyEditText.text.toString().isEmpty()) {
            status = false
            binding.bodyEditText.error = "Required"
        }
        return status
    }
}