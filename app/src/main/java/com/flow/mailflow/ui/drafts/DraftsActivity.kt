package com.flow.mailflow.ui.drafts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.response_data.DraftItem
import com.flow.mailflow.databinding.ActivityDraftsBinding
import com.flow.mailflow.ui.confirm.ConfirmActivity
import com.flow.mailflow.ui.home.components.WavClass
import java.io.File

class DraftsActivity : BaseActivity() {

    private lateinit var binding: ActivityDraftsBinding
    private val viewModel: DraftsViewModel by viewModels()
    private lateinit var draftsAdapter: DraftsAdapter

    // Recording utilities
    private lateinit var wavObj: WavClass
    private var isRecording = false
    private var recordedFilePath: String? = null
    companion object{
        const val EMAIL_CONTENT = "emailContentFromDraft"
        const val EMAIL_SUBJECT = "emailSubjectFromDraft"
        const val EMAIL_TO = "emailToFromDraft"
        const val DRAFT_ID = "draftId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDraftsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()

        // Initial load
        fetchAllDrafts()

        // Initialize Audio Recorder
        wavObj = WavClass(filesDir.path)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        draftsAdapter = DraftsAdapter(
            onItemClick = { draft ->
               startActivity(Intent(this, ConfirmActivity::class.java).apply {
                  putExtra(EMAIL_CONTENT, draft.body)
                  putExtra(EMAIL_SUBJECT, draft.subject)
                  putExtra(EMAIL_TO, draft.toEmail)
                   putExtra(DRAFT_ID, draft.id)

               })
            },
            onDeleteClick = { draft ->
                confirmDelete(draft)
            }
        )
        binding.draftsRecyclerView.adapter = draftsAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearch() {
        // Text Search on Done/Enter
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(queryText = v.text.toString(), audioFile = null)
                true
            } else false
        }

        // Also watch for text changes if you want real-time (optional)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) fetchAllDrafts() // Reset when cleared
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Voice Search (Hold to Record)
        binding.voiceSearchButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (checkPermissions()) {
                        startRecording()
                    } else {
                        requestAudioPermissions()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isRecording) {
                        stopRecordingAndSearch()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchAllDrafts() {
        viewModel.getAllDrafts().observe(this) { state ->
            handleState(state) { response ->
                response.data?.results?.let { draftsAdapter.submitList(it) }
            }
        }
    }

    private fun performSearch(queryText: String?, audioFile: File?) {
        viewModel.searchDrafts(queryText, audioFile).observe(this) { state ->
            handleState(state) { response ->
                binding.searchEditText.setText( response.data?.queryUsed ?: " ")
                // Assuming SearchDraftsResponse has a 'results' list similar to GetDraftsResponse
                response.data?.results?.let {
                   val filtered =  it.filter { (it.score ?: 0.0) >= 0.25 }
                        .sortedByDescending { it.score }

                    draftsAdapter.submitList(filtered) }
            }
        }
    }

    private fun confirmDelete(draft: DraftItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Draft")
            .setMessage("Are you sure you want to delete '${draft.subject}'?")
            .setPositiveButton("Delete") { _, _ ->
                draft.id?.let { viewModel.deleteDraft(it) }?.observe(this) { state ->
                    when (state.status) {
                        Status.LOADING -> showLoading(true)
                        Status.SUCCESS -> {
                            showLoading(false)
                            // Remove from list locally or refresh
                            fetchAllDrafts()
                        }
                        Status.ERROR -> {
                            showLoading(false)
                            toastError(this, state.message ?: "Error deleting")
                        }
                        else -> {}
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- Recorder Logic (Reused/Simplified) ---
    private fun startRecording() {
        isRecording = true
        binding.recordingLottie.visibility = View.VISIBLE
        wavObj.startRecording()
    }

    private fun stopRecordingAndSearch() {
        isRecording = false
        binding.recordingLottie.visibility = View.GONE
        recordedFilePath = wavObj.stopRecording()

        if (!recordedFilePath.isNullOrEmpty()) {
            performSearch(queryText = null, audioFile = File(recordedFilePath!!))
        }
    }

    // --- Permissions (Simplified) ---
    private fun checkPermissions() = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
    }

    // Generic State Handler for less boilerplate
    private fun <T> handleState(state: com.flow.mailflow.api.ApiState<T>, onSuccess: (T) -> Unit) {
        when (state.status) {
            Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
            Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                state.response?.let { onSuccess(it) }
            }
            Status.ERROR -> {
                binding.progressBar.visibility = View.GONE
                toastError(this, state.message ?: "Unknown error")
            }
            else -> binding.progressBar.visibility = View.GONE
        }
    }


}