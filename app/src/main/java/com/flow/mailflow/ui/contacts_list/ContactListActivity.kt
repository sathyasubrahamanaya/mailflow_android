package com.flow.mailflow.ui.contacts_list

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.response_data.sub_response.GetContactsResponse
import com.flow.mailflow.databinding.ActivityContactListBinding
import com.flow.mailflow.ui.add_contacts.AddContactActivity
import com.flow.mailflow.ui.contacts_list.components.ContactsAdapter

class ContactListActivity : BaseActivity() {
    lateinit var binding: ActivityContactListBinding
    private val vm: ContactListVIewModel by viewModels()

    override fun onResume() {
        super.onResume()
        getContactsApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getContactsApi()
        binding.fabAddContact.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getContactsApi() {
        vm.getContacts().observe(this) {
            when (it.status) {
                Status.LOADING -> {
                   showLoading(true)
                }
                Status.SUCCESS -> {
                    if(it.response?.data?.contacts?.isNotEmpty() == true){
                        binding.contactRecyclerView.adapter = ContactsAdapter(this, it.response.data?.contacts!!) {
                        }

                    }
                }
                Status.COMPLETED -> {
                    showLoading(false)
                }
                Status.ERROR -> {
                    showLoading(false)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }}
    }
}