package com.almizan.mobile.front.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Marche
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _stats = MutableLiveData<Resource<Map<String, Int>>>()
    val stats: LiveData<Resource<Map<String, Int>>> = _stats

    private val _recentMarches = MutableLiveData<Resource<List<Marche>>>()
    val recentMarches: LiveData<Resource<List<Marche>>> = _recentMarches

    fun loadDashboard() {
        _stats.value = Resource.Loading
        _recentMarches.value = Resource.Loading

        viewModelScope.launch {
            try {
                val marchesDeferred = async {
                    try { api.getMarches(page = 1, limit = 5) } catch (e: Exception) { null }
                }
                val soumissionsDeferred = async {
                    try { api.getMesSoumissions() } catch (e: Exception) { null }
                }

                // ... inside loadDashboard() ...

                val marchesResponse = marchesDeferred.await()
                val soumissionsResponse = soumissionsDeferred.await()

// ✅ Fix: Extract .data from the PaginatedResponse
                val marches: List<Marche> =
                    if (marchesResponse?.isSuccessful == true) {
                        marchesResponse.body()?.data ?: emptyList()
                    } else {
                        emptyList()
                    }

                _recentMarches.value = Resource.Success(marches)
                // ✅ body() est directement une List<Soumission>
                val soumissions = if (soumissionsResponse?.isSuccessful == true)
                    soumissionsResponse.body() ?: emptyList()
                else emptyList()

                val recoursPossibles = soumissions.count {
                    it.status == "EVALUATED" || it.status == "REJECTED"
                }

                _stats.value = Resource.Success(
                    mapOf(
                        "marchesOuverts" to marches.count { it.status == "PUBLISHED" },
                        "mesSoumissions" to soumissions.size,
                        "recoursPossibles" to recoursPossibles
                    )
                )

            } catch (e: Exception) {
                _stats.value = Resource.Error("Connexion impossible")
                _recentMarches.value = Resource.Error("Connexion impossible")
            }
        }
    }
}