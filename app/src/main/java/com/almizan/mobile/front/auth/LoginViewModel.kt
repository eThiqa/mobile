package com.almizan.mobile.front.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.LoginRequest
import com.almizan.mobile.utils.Resource
import com.almizan.mobile.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)
    private val session = SessionManager(app)

    private val _loginState = MutableLiveData<Resource<Boolean>>()
    val loginState: LiveData<Resource<Boolean>> = _loginState

    private val _requiresOtp = MutableLiveData<String?>()
    val requiresOtp: LiveData<String?> = _requiresOtp

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Veuillez remplir tous les champs")
            return
        }
        _loginState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.requiresOtp) {
                            _requiresOtp.value = email
                        } else {
                            val token = body.resolveToken()  // ← fixed
                            val user = body.user
                            if (user != null && token.isNotEmpty()) {
                                session.saveSession(
                                    token = body.resolveToken(),  // ← fixed
                                    userId = user.id,
                                    name = "${user.first_name} ${user.last_name}",
                                    role = user.role,
                                    email = user.email
                                )
                                _loginState.value = Resource.Success(true)
                            } else {
                                _loginState.value = Resource.Error("Réponse invalide")
                            }
                        }
                    } else {
                        _loginState.value = Resource.Error("Réponse invalide")
                    }
                } else {
                    val msg = when (response.code()) {
                        401 -> "Email ou mot de passe incorrect"
                        403 -> "Compte non validé"
                        else -> "Erreur serveur (${response.code()})"
                    }
                    _loginState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Impossible de joindre le serveur")
            }
        }
    }
}