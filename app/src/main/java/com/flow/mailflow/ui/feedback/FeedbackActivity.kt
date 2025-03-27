package com.flow.mailflow.ui.feedback

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.R
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.request_data.FeedBackRequests
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.databinding.ActivityFeedbackBinding
import com.flow.mailflow.ui.login.LoginViewModel

class FeedbackActivity : BaseActivity() {
    private val vm: FeedbackViewModel by viewModels()
    lateinit var binding: ActivityFeedbackBinding
    var ratingValue: Int = 1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.sendFeedbackButton.setOnClickListener {
            addFeedback()
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            ratingValue = rating.toInt()
        }


    }


    private fun addFeedback() {
        val requests = FeedBackRequests(
            rating = ratingValue,
            comment = binding.commentEditText.text.toString(),
        )
        vm.feedback(requests).observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    //showLoading(false)
                }

                Status.ERROR -> {
                    showLoading(false)
                    toastError(this, it.response?.message ?: it.message)
                }

                Status.SUCCESS -> {
                    if (it.response?.errorcode == 0) {
                        Toast.makeText(this, it.response.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        toastError(this, it.response?.message ?: it.message)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}