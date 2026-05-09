package com.almizan.mobile.front.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityLoginBinding
import com.almizan.mobile.front.MainActivity
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.almizan.mobile.front.auth.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.requiresOtp.observe(this) { email ->
            if (email != null) {
                showLoading(false)
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
    }
}