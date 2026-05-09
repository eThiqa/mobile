package com.almizan.mobile.front.dashboard


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _stats = MutableLiveData<Resource<Map<String, Int>>>()
    val stats: LiveData<Resource<Map<String, Int>>> = _stats

    fun loadDashboard() {
        _stats.value = Resource.Loading
        viewModelScope.launch {
            try {
                val marchesDeferred = async { api.getMarches(statut = "EN_COURS") }
                val soumissionsDeferred = async { api.getMesSoumissions() }

                val marchesResponse = marchesDeferred.await()
                val soumissionsResponse = soumissionsDeferred.await()

                val marchesOuverts = if (marchesResponse.isSuccessful)
                    marchesResponse.body()?.data?.size ?: 0 else 0

                val soumissions = if (soumissionsResponse.isSuccessful)
                    soumissionsResponse.body()?.data ?: emptyList() else emptyList()

                val mesSoumissions = soumissions.size
                val recoursPossibles = soumissions.count { it.recoursPossible && !it.recoursDepose }

                _stats.value = Resource.Success(mapOf(
                    "marchesOuverts" to marchesOuverts,
                    "mesSoumissions" to mesSoumissions,
                    "recoursPossibles" to recoursPossibles
                ))
            } catch (e: Exception) {
                _stats.value = Resource.Error("Connexion impossible")
            }
        }
    }
}