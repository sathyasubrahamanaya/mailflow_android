package com.flow.mailflow.ui.splash


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flow.mailflow.R
import com.flow.mailflow.databinding.ActivityMainBinding
import com.flow.mailflow.ui.home.HomeActivity
import com.flow.mailflow.ui.login.LoginActivity
import com.flow.mailflow.utils.SharedPreferenceHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        //val sharedPreference = getSharedPreferences("user", MODE_PRIVATE)
        val token = SharedPreferenceHelper().getString(SharedPreferenceHelper.TOKEN,"")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if(token != ""){
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }, 2000) // 5000 milliseconds = 5 seconds
    }

}
