package com.almizan.mobile.front.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.LoginRequest
import com.almizan.mobile.data.models.OtpRequest
import com.almizan.mobile.utils.Resource
import com.almizan.mobile.utils.SessionManager
import kotlinx.coroutines.launch

class OtpViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)
    private val session = SessionManager(app)

    private val _otpState = MutableLiveData<Resource<Boolean>>()
    val otpState: LiveData<Resource<Boolean>> = _otpState

    fun verifyOtp(email: String, otp: String) {
        _otpState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.verifyOtp(OtpRequest(email, otp))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.user != null) {
                        val user = body.user
                        session.saveSession(
                            token = body.resolveToken(),
                            userId = user.id,
                            name = "${user.first_name} ${user.last_name}",
                            role = user.role,
                            email = user.email,
                            nom = user.last_name,
                            prenom = user.first_name,
                            telephone = user.phone_number ?: ""
                        )
                        _otpState.value = Resource.Success(true)
                    } else {
                        _otpState.value = Resource.Error("Réponse invalide")
                    }
                } else {
                    val msg = when (response.code()) {
                        401 -> "Code OTP incorrect ou expiré"
                        429 -> "Trop de tentatives, réessayez plus tard"
                        else -> "Erreur (${response.code()})"
                    }
                    _otpState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _otpState.value = Resource.Error("Impossible de joindre le serveur")
            }
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            try {
                api.login(LoginRequest(email, ""))
            } catch (_: Exception) { }
        }
    }
}
