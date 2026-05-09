package com.almizan.mobile.front.suivi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Soumission
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class SuiviViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _soumissions = MutableLiveData<Resource<List<Soumission>>>()
    val soumissions: LiveData<Resource<List<Soumission>>> = _soumissions

    fun loadSoumissions() {
        _soumissions.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.getMesSoumissions()
                if (response.isSuccessful) {
                    _soumissions.value = Resource.Success(response.body()?.data ?: emptyList())
                } else {
                    _soumissions.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _soumissions.value = Resource.Error("Connexion impossible")
            }
        }
    }
}