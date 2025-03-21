package com.flow.mailflow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
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


        val text = binding.registerTextView.text.toString()
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event here
                // For example, navigate to the registration screen
                val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intent)
            }
        }

        val startIndex = text.indexOf("Register")
        val endIndex = startIndex + "Register".length

        // Create a ForegroundColorSpan for white color
        val whiteColorSpan = ForegroundColorSpan(Color.WHITE)

        // Apply the white color span
        spannableString.setSpan(
            whiteColorSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply the clickable span
        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.registerTextView.text = spannableString
        binding.registerTextView.movementMethod = LinkMovementMethod.getInstance()
    }


}
