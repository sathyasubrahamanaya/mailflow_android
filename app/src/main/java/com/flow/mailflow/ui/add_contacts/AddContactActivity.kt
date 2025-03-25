package com.flow.mailflow.ui.add_contacts

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.indices
import com.flow.mailflow.R
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.databinding.ActivityAddContactBinding
import com.flow.mailflow.ui.contacts_list.ContactListVIewModel
import com.flow.mailflow.utils.Utils.timberCall

class AddContactActivity : BaseActivity() {
    lateinit var binding: ActivityAddContactBinding
    private val vm: AddContactsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.submitButton.setOnClickListener {
            if (validate()){
                createContactApi()
            }
        }
    }

    private fun createContactApi() {
        val params = CreateContactRequest(
            name = binding.nameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            phone = binding.phoneEditText.text.toString()
        )

        vm.createContact(params).observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    showLoading(false)
                }

                Status.ERROR -> {
                    showLoading(false)
                    toastError(this, it.response?.message ?: it.message)
                }

                Status.SUCCESS -> {
                    if (it.response?.errorcode == 0) {
                        timberCall(this, "",it.response.message?:"")
                        finish()
                    } else {
                        toastError(this, it.response?.message ?: it.message)
                    }
                }
        }
        }
    }

     fun validate(): Boolean {
        var status = true
        if (binding.nameEditText.text.toString().isEmpty()) {
            status = false
            binding.nameEditText.error = "Required"
        }
        if (binding.emailEditText.text.toString().isEmpty()) {
            status = false
            binding.emailEditText.error = "Required"
        }
        if (binding.phoneEditText.text.toString().isEmpty()) {
            status = false
            binding.phoneEditText.error = "Required"
        }
        return status
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}