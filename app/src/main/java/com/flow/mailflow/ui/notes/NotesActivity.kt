package com.flow.mailflow.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.flow.mailflow.api.ApiState
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.response_data.NoteItem
import com.flow.mailflow.databinding.ActivityNotesBinding
import com.flow.mailflow.ui.home.components.WavClass
import java.io.File

class NotesActivity : BaseActivity() {

    private lateinit var binding: ActivityNotesBinding
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    // Recording utilities
    private lateinit var wavObj: WavClass
    private var isRecording = false
    private var recordedFilePath: String? = null

    private val REQUEST_PERMISSION_CODE = 1002

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()

        // Initialize Audio Recorder
        wavObj = WavClass(filesDir.path)

        // FAB: Create New Note
        binding.addNoteFab.setOnClickListener {
            startActivity(Intent(this, NoteDetailActivity::class.java))
        }

        // Initial Load
        fetchAllNotes()
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when returning from Detail screen
        // Only refresh if we aren't currently searching
        if (binding.searchEditText.text.isNullOrEmpty()) {
            fetchAllNotes()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            onItemClick = { note ->
                // Open Note Detail for Editing
                val intent = Intent(this, NoteDetailActivity::class.java).apply {
                    putExtra(NoteDetailActivity.KEY_NOTE_ID, note.id)
                    putExtra(NoteDetailActivity.KEY_NOTE_CONTENT, note.content)
                }
                startActivity(intent)
            },
            onDeleteClick = { note ->
                confirmDelete(note)
            }
        )
        binding.notesRecyclerView.adapter = notesAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearch() {
        // 1. Text Search (Enter Key)
        binding.searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(queryText = v.text.toString(), audioFile = null)
                true
            } else false
        }

        // 2. Real-time Text Watcher (Reset on clear)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) fetchAllNotes()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 3. Voice Search (Hold to Record)
        binding.voiceSearchButton.setOnTouchListener { _, motionEvent ->
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

    private fun fetchAllNotes() {
        viewModel.getAllNotes().observe(this) { state ->
            handleState(state) { response ->
                response.data?.results?.let { notesAdapter.submitList(it) }
            }
        }
    }

    private fun performSearch(queryText: String?, audioFile: File?) {
        viewModel.searchNotes(queryText, audioFile).observe(this) { state ->
            handleState(state) { response ->
                // Update Search Text from Voice Response (or clear if null)
                binding.searchEditText.setText(response.data?.queryUsed)

                // Filter and Sort Results
                response?.data?.results?.let { list ->
                    val filtered = list.filter { (it.score ?: 0.0) >= 0.25 }
                        .sortedByDescending { it.score }

                    if (filtered.isEmpty()) {
                        toastError(this, "No matching notes found")
                    }
                    notesAdapter.submitList(filtered)
                }
            }
        }
    }

    private fun confirmDelete(note: NoteItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                note.id?.let { id ->
                    viewModel.deleteNote(id).observe(this) { state ->
                        // Using explicit handling for Delete to match DraftsActivity pattern with showLoading
                        when (state.status) {
                            Status.LOADING -> showLoading(true)
                            Status.SUCCESS -> {
                                showLoading(false)
                                fetchAllNotes() // Refresh list
                            }
                            Status.ERROR -> {
                                showLoading(false)
                                toastError(this, state.message ?: "Error deleting")
                            }
                            else -> {}
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- Recorder Logic ---
    private fun startRecording() {
        isRecording = true
        binding.recordingLottie.visibility = View.VISIBLE
        binding.recordingLottie.playAnimation()
        // Dim input to indicate voice mode
        binding.searchTextInputLayout.alpha = 0.5f
        wavObj.startRecording()
    }

    private fun stopRecordingAndSearch() {
        isRecording = false
        binding.recordingLottie.visibility = View.GONE
        binding.recordingLottie.cancelAnimation()
        binding.searchTextInputLayout.alpha = 1.0f

        recordedFilePath = wavObj.stopRecording()

        if (!recordedFilePath.isNullOrEmpty()) {
            performSearch(queryText = null, audioFile = File(recordedFilePath!!))
        }
    }

    // --- Permissions ---
    private fun checkPermissions() = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
    }

    // Generic State Handler for less boilerplate (Matched to DraftsActivity)
    private fun <T> handleState(state: ApiState<T>, onSuccess: (T) -> Unit) {
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