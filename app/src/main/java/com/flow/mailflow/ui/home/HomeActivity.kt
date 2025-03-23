package com.flow.mailflow.ui.home

//noinspection SuspiciousImport
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.flow.mailflow.databinding.ActivityHomeBinding
import com.flow.mailflow.ui.contacts_list.ContactListActivity
import com.flow.mailflow.ui.support.SupportActivity
import com.flow.mailflow.ui.confirm.ConfirmActivity
import com.flow.mailflow.ui.feedback.FeedbackActivity
import com.flow.mailflow.utils.Utils.timberCall

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFilePath: String = ""
    private var outputFileName: String = ""

    // Properties for toggling playback
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingFile = false
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener { menuItem ->
            startActivity(Intent(this, ContactListActivity::class.java))
            true
        }
        binding.toolbar.menu.getItem(1).setOnMenuItemClickListener { menuItem ->
            startActivity(Intent(this, SupportActivity::class.java))
            true
        }
        binding.toolbar.menu.getItem(2).setOnMenuItemClickListener { menuItem ->
            startActivity(Intent(this, FeedbackActivity::class.java))
            true
        }



        // Set click listeners
        binding.recordButton.setOnClickListener {
            if (!isRecording){
                if (checkPermissions()) {
                    binding.lottieAnimationView.playAnimation()
                    startRecording()
                } else {
                    // Check if we should show rationale or if the permission was permanently denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) ||
                        !ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        showPermissionSettingsDialog()
                    } else {
                        requestPermissions()
                    }
                }
            }else{
                stopRecording()
            }

        }


        binding.filePlayView.setOnClickListener {
            togglePlayback(outputFilePath)
        }
        binding.closeButton.setOnClickListener {
            binding.cardView.isVisible = false
            outputFilePath = ""
            outputFileName = ""
        }
        binding.sendButton.setOnClickListener {
            startActivity(Intent(this, ConfirmActivity::class.java))
        }


    }





    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        if (isRecording) return

        outputFilePath = "${externalCacheDir?.absolutePath}/recording_${System.currentTimeMillis()}.mp3"
        outputFileName = "recording_${System.currentTimeMillis()}.mp3"

        mediaRecorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(256000)
            setAudioSamplingRate(44100)
            setOutputFile(outputFilePath)
            prepare()
        }
        mediaRecorder?.start()
        isRecording = true
        binding.recordButton.text = "Stop Recording"

        // Update progress bar (simple simulation)
        Thread {
            while (isRecording) {
                Thread.sleep(100)
                runOnUiThread {

                }
            }
        }.start()
    }

    private fun stopRecording() {
        if (!isRecording) return

        mediaRecorder?.apply {
            stop()
            release()
        }
        binding.lottieAnimationView.cancelAnimation()
        binding.lottieAnimationView.progress = 0f
        mediaRecorder = null
        isRecording = false
        binding.recordButton.text = "Start Recording"
        binding.cardView.isVisible = true
        binding.fileNameTextView.text = outputFileName
    }

    private fun togglePlayback(filePath: String) {
        if (mediaPlayer == null) {
            // Initialize MediaPlayer and start playback
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                isPlayingFile = true
                binding.filePlayView.text = "Pause"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@HomeActivity, com.flow.mailflow.R.color.error))
                binding.lottieAnimationPlayer.playAnimation()
            }
        } else {
            // Toggle play/pause
            if (isPlayingFile) {
                mediaPlayer?.pause()
                binding.filePlayView.text = "Play"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@HomeActivity, com.flow.mailflow.R.color.green))
                binding.lottieAnimationPlayer.pauseAnimation()
                binding.lottieAnimationPlayer.progress = 0f


            } else {
                mediaPlayer?.start()
                binding.filePlayView.text = "Pause"
                binding.filePlayView.setTextColor(ContextCompat.getColor(this@HomeActivity, com.flow.mailflow.R.color.error))
                binding.lottieAnimationPlayer.playAnimation()
            }
            isPlayingFile = !isPlayingFile
        }

        // Release MediaPlayer when playback is complete
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
            isPlayingFile = false
        }
    }


    private fun checkPermissions(): Boolean {
        val recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return recordPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE)
    }

    // Called when the user responds to the permission request.
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, start recording if needed
                startRecording()
            } else {
                timberCall(this,"Permissions required","Permissions are required to record audio.",true)
            }
        }
    }

    // Show a dialog directing the user to the app settings
    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Audio recording permissions are permanently denied. Please enable them in the app settings.")
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
    }


}