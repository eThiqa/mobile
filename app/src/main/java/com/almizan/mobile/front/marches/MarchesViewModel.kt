package com.almizan.mobile.front.marches

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Marche
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class MarchesViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _marches = MutableLiveData<Resource<List<Marche>>>()
    val marches: LiveData<Resource<List<Marche>>> = _marches

    private var currentSearch: String? = null
    private var currentStatut: String? = null

    fun loadMarches() {        // Set loading state first
        _marches.value = Resource.Loading

        // Start the coroutine on a new line
        viewModelScope.launch {
            try {
                android.util.Log.d("DEBUG_LOG", "Searching: $currentSearch, Status: $currentStatut")

                val response = api.getMarches(
                    search = currentSearch,
                    statut = currentStatut
                )

                if (response.isSuccessful) {
                    val paginatedBody = response.body()
                    // Extract data list from the PaginatedResponse object
                    val list: List<Marche> = paginatedBody?.data ?: emptyList()

                    _marches.value = Resource.Success(list)
                } else {
                    android.util.Log.e("DEBUG_LOG", "Response code: ${response.code()}")
                    _marches.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_LOG", "Logic Error: ", e)
                _marches.value = Resource.Error("Connexion impossible: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        currentSearch = query.ifBlank { null }
        loadMarches()
    }

    fun filterByStatut(statut: String?) {
        currentStatut = statut
        loadMarches()
    }
}