package com.flow.mailflow.ui.confirm

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.databinding.ActivityConfirmBinding
import com.flow.mailflow.utils.Utils.timberCall

class ConfirmActivity : AppCompatActivity() {
    lateinit var binding: ActivityConfirmBinding
    val recipient = "abin@webcastle.in"
    val ccRecipients = arrayOf("cc1@example.com", "cc2@example.com")
    val subject = "Your Subject Here"
    val body = "Your email body here."

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

        binding.sendToGmailButton.setOnClickListener {
            timberCall(this, "Click", "onClick")
            val uriText =
                "mailto:$recipient" + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body)
            "&cc=" + Uri.encode(ccRecipients.joinToString(","))
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriText))
            startActivity(emailIntent)

        }

    }
}