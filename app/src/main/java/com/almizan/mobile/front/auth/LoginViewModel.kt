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

    // true = OTP requis, false = connecté directement
    private val _requiresOtp = MutableLiveData<String?>() // email si OTP requis
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
                    val body = response.body()?.data
                    if (body != null) {
                        if (body.requiresOtp) {
                            _requiresOtp.value = email
                        } else {
                            session.saveSession(
                                token = body.token,
                                userId = body.user.id,
                                name = "${body.user.prenom} ${body.user.nom}",
                                role = body.user.role,
                                email = body.user.email
                            )
                            _loginState.value = Resource.Success(true)
                        }
                    } else {
                        _loginState.value = Resource.Error("Réponse invalide")
                    }
                } else {
                    val msg = when (response.code()) {
                        401 -> "Email ou mot de passe incorrect"
                        403 -> "Compte non validé par l'administrateur"
                        else -> "Erreur serveur (${response.code()})"
                    }
                    _loginState.value = Resource.Error(msg, response.code())
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error("Impossible de joindre le serveur")
            }
        }
    }
}