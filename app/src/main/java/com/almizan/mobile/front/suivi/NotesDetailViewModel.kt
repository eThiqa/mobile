package com.almizan.mobile.front.suivi


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class NotesDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _notes = MutableLiveData<Resource<Map<String, Any>>>()
    val notes: LiveData<Resource<Map<String, Any>>> = _notes

    fun loadNotes(soumissionId: String) {
        _notes.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.getNotesDetail(soumissionId)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) _notes.value = Resource.Success(data)
                    else _notes.value = Resource.Error("Données introuvables")
                } else {
                    _notes.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _notes.value = Resource.Error("Connexion impossible")
            }
        }
    }
}