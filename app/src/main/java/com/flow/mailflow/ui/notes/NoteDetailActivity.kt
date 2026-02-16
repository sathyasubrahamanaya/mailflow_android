package com.flow.mailflow.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.data_models.response_data.NoteData
import com.flow.mailflow.databinding.ActivityNoteDetailBinding
import com.flow.mailflow.ui.home.components.WavClass
import java.io.File

class NoteDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private val viewModel: NotesViewModel by viewModels()

    // Data State
    private var currentNoteId: String? = null
    private var isEditMode = false

    // Recorder State
    private lateinit var wavObj: WavClass
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingFile = false
    private var recordedFilePath: String? = null

    companion object {
        const val KEY_NOTE_ID = "NOTE_ID"
        const val KEY_NOTE_CONTENT = "NOTE_CONTENT"
        private const val REQUEST_PERMISSION_CODE = 1003
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup UI based on Intent (Add vs Edit)
        currentNoteId = intent.getStringExtra(KEY_NOTE_ID)
        val content = intent.getStringExtra(KEY_NOTE_CONTENT)

        if (currentNoteId != null) {
            setupEditMode(content)
        } else {
            setupAddMode()
        }

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // 2. Initialize Recorder
        wavObj = WavClass(filesDir.path)

        // 3. Setup Listeners
        setupClickListeners()
    }

    private fun setupAddMode() {
        isEditMode = false
        binding.toolbar.title = "New Note"
        binding.saveButton.text = "Save Note"
    }

    private fun setupEditMode(content: String?) {
        isEditMode = true
        binding.toolbar.title = "Edit Note"
        binding.saveButton.text = "Update Note"
        if (!content.isNullOrEmpty()) {
            binding.contentEditText.setText(content)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupClickListeners() {
        // Record Button Logic
        binding.recordButton.setOnClickListener {
            if (!isRecording) {
                if (checkPermissions()) {
                    binding.lottieAnimationView.playAnimation()
                    startRecordingUI()
                } else {
                    requestPermissions()
                }
            } else {
                stopRecordingUI()
            }
        }

        // Playback Logic
        binding.filePlayView.setOnClickListener {
            recordedFilePath?.let { path -> togglePlayback(path) }
        }

        // Delete/Close Logic
        binding.closeButton.setOnClickListener {
            resetRecordingState()
        }

        // Save Button Logic
        binding.saveButton.setOnClickListener {
            val finalContent = binding.contentEditText.text.toString()
            val fileToSend = if (recordedFilePath != null) File(recordedFilePath!!) else null

            if (finalContent.isNotEmpty() || fileToSend != null) {
                if (isEditMode && currentNoteId != null) {
                    performUpdate(currentNoteId!!, finalContent, fileToSend)
                } else {
                    performSave(finalContent, fileToSend)
                }
            } else {
                toastError(this, "Please enter text or record audio")
            }
        }
    }

    // --- Recording Logic ---

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecordingUI() {
        if (isRecording) return

        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingFile = false

        wavObj.startRecording()
        isRecording = true
        binding.recordButton.text = "Stop Recording"
    }

    private fun stopRecordingUI() {
        if (!isRecording) return

        val path = wavObj.stopRecording()
        binding.lottieAnimationView.cancelAnimation()
        binding.lottieAnimationView.progress = 0f
        mediaRecorder = null
        isRecording = false

        recordedFilePath = path
        binding.recordButton.text = "Start Record"

        binding.cardView.isVisible = true
        binding.lottieAnimationView.isVisible = false
        binding.micIcon.isVisible = false
        binding.recordButton.isVisible = false
        binding.fileNameTextView.text = "Voice Note Recorded"
    }

    private fun resetRecordingState() {
        binding.cardView.isVisible = false
        recordedFilePath = null

        binding.lottieAnimationView.isVisible = true
        binding.micIcon.isVisible = true
        binding.recordButton.isVisible = true

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayingFile = false
        binding.lottieAnimationPlayer.cancelAnimation()
    }

    private fun togglePlayback(filePath: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                isPlayingFile = true
                binding.filePlayView.text = "Pause"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@NoteDetailActivity, com.flow.mailflow.R.color.error))
                binding.lottieAnimationPlayer.playAnimation()
            }
        } else {
            if (isPlayingFile) {
                mediaPlayer?.pause()
                binding.filePlayView.text = "Play"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@NoteDetailActivity, com.flow.mailflow.R.color.green))
                binding.lottieAnimationPlayer.pauseAnimation()
            } else {
                mediaPlayer?.start()
                binding.filePlayView.text = "Pause"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@NoteDetailActivity, com.flow.mailflow.R.color.error))
                binding.lottieAnimationPlayer.playAnimation()
            }
            isPlayingFile = !isPlayingFile
        }

        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
            isPlayingFile = false
            binding.filePlayView.text = "Play"
            binding.filePlayView.setTextColor(ContextCompat.getColor(this@NoteDetailActivity, com.flow.mailflow.R.color.green))
            binding.lottieAnimationPlayer.cancelAnimation()
            binding.lottieAnimationPlayer.progress = 0f
        }
    }

    // --- API Operations ---

    private fun performSave(content: String, file: File?) {
        viewModel.saveNote(content, file).observe(this) { state ->
            when (state.status) {
                Status.LOADING -> {
                    showLoading(true)
                }
                Status.COMPLETED -> {
                    showLoading(false)
                }
                Status.SUCCESS -> {
                    if (state.response?.errorcode == 0) {
                        toastError(this, "Note Saved Successfully")

                        // Switch to Edit Mode using the returned ID

                        recordedFilePath = null
                        onBackPressed()
                    } else {
                        toastError(this, state.response?.message ?: "Error saving note")
                    }
                }
                Status.ERROR -> {
                    toastError(this, state.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun performUpdate(id: String, content: String, file: File?) {
        viewModel.updateNote(id, content, file).observe(this) { state ->
            when (state.status) {
                Status.LOADING -> {
                    showLoading(true)
                }
                Status.COMPLETED -> {
                    showLoading(false)
                }
                Status.SUCCESS -> {
                    if (state.response?.errorcode == 0) {
                        toastError(this, "Note Updated Successfully")
                        onBackPressed()
                        recordedFilePath = null
                    } else {
                        toastError(this, state.response?.message ?: "Error updating note")
                    }
                }
                Status.ERROR -> {
                    toastError(this, state.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !isLoading
    }

    // --- Permissions ---
    private fun checkPermissions() = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permission granted
            } else {
                toastError(this, "Microphone permission is required")
            }
        }
    }
}