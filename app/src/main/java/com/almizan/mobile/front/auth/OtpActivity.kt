package com.almizan.mobile.front.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityOtpBinding
import com.almizan.mobile.front.MainActivity
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private val viewModel: OtpViewModel by viewModels()
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email") ?: ""
        binding.tvEmailInfo.text = "Code OTP envoyé à $email"

        startCountdown()

        binding.btnVerify.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp.length < 6) {
                binding.etOtp.error = "Code invalide"
                return@setOnClickListener
            }
            viewModel.verifyOtp(email, otp)
        }

        binding.tvResend.setOnClickListener {
            viewModel.resendOtp(email)
            startCountdown()
        }

        viewModel.otpState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
                is Resource.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startCountdown() {
        binding.tvResend.isEnabled = false
        object : CountDownTimer(60_000, 1_000) {
            override fun onTick(ms: Long) {
                binding.tvResend.text = "Renvoyer dans ${ms / 1000}s"
            }
            override fun onFinish() {
                binding.tvResend.text = "Renvoyer le code"
                binding.tvResend.isEnabled = true
            }
        }.start()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnVerify.isEnabled = !show
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}