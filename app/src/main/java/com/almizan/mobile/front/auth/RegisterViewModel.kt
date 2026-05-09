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
        nom: String,
        prenom: String,
        email: String,
        telephone: String,
        password: String,
        raisonSociale: String,
        registreCommerce: String,
        secteurActivite: String
    ) {
        _registerState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.register(
                    RegisterRequest(
                        nom = nom,
                        prenom = prenom,
                        email = email,
                        telephone = telephone,
                        password = password,
                        raisonSociale = raisonSociale,
                        registreCommerce = registreCommerce,
                        secteurActivite = secteurActivite
                    )
                )
                if (response.isSuccessful) {
                    _registerState.value = Resource.Success(
                        "Compte créé avec succès ! En attente de validation."
                    )
                } else {
                    val msg = when (response.code()) {
                        409 -> "Un compte existe déjà avec cet email"
                        422 -> "Données invalides. Vérifiez le formulaire."
                        else -> "Erreur serveur (${response.code()})"
                    }
                    _registerState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _registerState.value = Resource.Error("Impossible de joindre le serveur")
            }
        }
    }
}