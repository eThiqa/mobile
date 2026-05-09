package com.almizan.mobile.front.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.almizan.mobile.front.MainActivity
import com.almizan.mobile.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pas de layout : juste le background du theme

        lifecycleScope.launch {
            delay(1500)
            val session = SessionManager(this@SplashActivity)
            val intent = if (session.isLoggedIn()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}