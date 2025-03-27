package com.flow.mailflow.ui.support

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.databinding.ActivitySupportBinding
import com.flow.mailflow.databinding.DialogAddQueryBinding
import com.flow.mailflow.ui.support.components.QueriesAdapter
import com.flow.mailflow.utils.Utils.timberCall

class SupportActivity : BaseActivity() {
    lateinit var binding: ActivitySupportBinding
    private val vm: SupportViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getQueriesApi()

        binding.fabAddQuery.setOnClickListener {
            showDialogQuery()
        }

    }

    private fun getQueriesApi() {
        vm.getQueries().observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.SUCCESS -> {
                    showLoading(false)
                    if (it.response?.data?.queries?.isNotEmpty() == true) {
                        binding.queriesRecyclerView.adapter = QueriesAdapter(this, it.response.data?.queries!!) {
                            //showDialogQuery()
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
            }
        }
    }

    private fun showDialogQuery() {
            try {
                val bindingss = DialogAddQueryBinding.inflate(layoutInflater)
                //AlertDialogBuilder
                val messageBoxBuilder = AlertDialog.Builder(this).setView(bindingss.root)
                val messageBoxInstance = messageBoxBuilder.show()
                messageBoxBuilder.setCancelable(false)

                bindingss.submitButton.setOnClickListener {
                    if(bindingss.queryEditText.text.toString().isNotEmpty()) {
                        addQueryApi(bindingss.queryEditText.text.toString())
                        messageBoxInstance.dismiss()
                    }else{
                        bindingss.queryEditText.error = "Required"
                    }
                }

            } catch (e: java.lang.Exception) {
                timberCall(this@SupportActivity,"loadingDialogException", e.toString())
            }


    }

    private fun addQueryApi(query: String) {
        vm.createQueries(query).observe(this) {
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
                        getQueriesApi()
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