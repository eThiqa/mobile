package com.almizan.mobile.front.marches

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Marche
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarchesViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _marches = MutableLiveData<Resource<List<Marche>>>()
    val marches: LiveData<Resource<List<Marche>>> = _marches

    private var allMarches: List<Marche> = emptyList()
    private var currentStatut: String? = null
    private var searchJob: Job? = null

    fun loadMarches() {
        _marches.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.getMarches()
                if (response.isSuccessful) {
                    allMarches = response.body()?.data ?: emptyList()
                    applyFilters()
                } else {
                    _marches.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _marches.value = Resource.Error("Connexion impossible")
            }
        }
    }

    fun filterByStatut(statut: String?) {
        currentStatut = statut
        val filtered = if (statut == null) allMarches
        else allMarches.filter { it.status == statut }
        _marches.value = Resource.Success(filtered)
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val filtered = allMarches.filter { marche ->
                (currentStatut == null || marche.status == currentStatut) &&
                        (query.isBlank() ||
                                marche.getTitre().contains(query, ignoreCase = true) ||
                                marche.getReference().contains(query, ignoreCase = true))
            }
            _marches.value = Resource.Success(filtered)
        }
    }

    private fun applyFilters() {
        val filtered = if (currentStatut == null) allMarches
        else allMarches.filter { it.status == currentStatut }  // was: it.statut.name
        _marches.value = Resource.Success(filtered)
    }
}