package com.almizan.mobile.front.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.RegisterRequest
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _registerState = MutableLiveData<Resource<String>>()
    val registerState: LiveData<Resource<String>> = _registerState

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        _registerState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.register(
                    RegisterRequest(
                        email = email,
                        password = password,
                        first_name = firstName,
                        last_name = lastName
                    )
                )
                if (response.isSuccessful) {
                    _registerState.value = Resource.Success("Compte créé avec succès !")
                } else {
                    _registerState.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _registerState.value = Resource.Error("Connexion impossible")
            }
        }
    }
}