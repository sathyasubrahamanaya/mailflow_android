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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.flow.mailflow.api.Status
import com.flow.mailflow.base_utility.BaseActivity
import com.flow.mailflow.databinding.ActivityHomeBinding
import com.flow.mailflow.ui.contacts_list.ContactListActivity
import com.flow.mailflow.ui.support.SupportActivity
import com.flow.mailflow.ui.confirm.ConfirmActivity
import com.flow.mailflow.ui.feedback.FeedbackActivity
import com.flow.mailflow.ui.home.components.WavClass
import com.flow.mailflow.utils.Utils.timberCall
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFilePath: String = ""
    private var outputFileName: String = ""

    // Properties for toggling playback
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingFile = false
    private val vm: HomeViewModel by viewModels()

    lateinit var wavObj: WavClass
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

        wavObj = WavClass(filesDir.path)

        // Set click listeners
        binding.recordButton.setOnClickListener {
            if (!isRecording){
                if (checkPermissions()) {
                    binding.lottieAnimationView.playAnimation()
                    startRecordingUI()
                } else {
                    // Check if we should show rationale or if the permission was permanently denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        )
                    ) {
                        showPermissionSettingsDialog()
                    } else {
                        requestPermissions()
                    }
                }
            }else{
                wavObj.stopRecording()
                stopRecordingUI()
            }

        }


        binding.filePlayView.setOnClickListener {
            wavObj.getPath(wavObj.tempWavFile)?.let { it1 -> togglePlayback(it1) }
        }
        binding.closeButton.setOnClickListener {
            binding.cardView.isVisible = false
            outputFilePath = ""
            outputFileName = ""
        }
        binding.sendButton.setOnClickListener {
            if(outputFilePath.isNotEmpty()){
                generateFileApi(File(outputFilePath))
            }else{
                generateFileApi()
            }
        }


    }

    private fun generateFileApi(convertedFile: File? = null) {
        vm.generateMail(
            binding.recipientEditText.text.toString(),
            binding.instructionsEditText.text.toString(),
            "",
            convertedFile
        ).observe(this){
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                }

                Status.COMPLETED -> {
                    showLoading(false)
                }

                Status.ERROR -> {
                    toastError(this, it.response?.message ?: it.message)
                }

                Status.SUCCESS -> {
                    if (it.response?.errorcode == 0) {
                        val json = Gson().toJson(it.response.data)
                        Timber.tag("SuccessFromHome").e(json)

                        val intent = Intent(this, ConfirmActivity::class.java).apply {
                            putExtra("emailContent", json)
                        }
                        startActivity(intent)
                    }else{
                        toastError(this, it.response?.message ?: it.message)
                    }
                }
            }
        }
    }





    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecordingUI() {
        if (isRecording) return
        mediaPlayer = null
        isPlayingFile = false
        wavObj.startRecording()
        isRecording = true
        binding.recordButton.text = "Stop Recording"
    }

    private fun stopRecordingUI() {
        if (!isRecording) return
        binding.lottieAnimationView.cancelAnimation()
        binding.lottieAnimationView.progress = 0f
        mediaRecorder = null
        isRecording = false
        outputFilePath=  wavObj.stopRecording()?: ""
        stopRecordingUI()
        binding.recordButton.text = "Start Recording"
        binding.cardView.isVisible = true
        binding.fileNameTextView.text = outputFilePath
    }

    private fun togglePlayback(filePath: String) {
        if (isRecording) return
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
            binding.filePlayView.text = "Play"
            binding.filePlayView.setTextColor(ContextCompat.getColor(this@HomeActivity, com.flow.mailflow.R.color.green))
            binding.lottieAnimationPlayer.pauseAnimation()
            binding.lottieAnimationPlayer.progress = 0f
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
                startRecordingUI()
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