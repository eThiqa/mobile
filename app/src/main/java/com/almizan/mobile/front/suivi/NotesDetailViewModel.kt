package com.almizan.mobile.front.suivi

import android.app.Application
import androidx.lifecycle.*
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class NotesDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _notes =
        MutableLiveData<Resource<Map<String, Any>>>()

    val notes: LiveData<Resource<Map<String, Any>>> =
        _notes

    fun loadNotes(soumissionId: String) {

        _notes.value = Resource.Loading

        viewModelScope.launch {

            try {

                val response =
                    api.getSoumission(soumissionId)

                if (response.isSuccessful) {

                    val soumission =
                        response.body()?.data

                    if (soumission != null) {

                        val notesMap =
                            mapOf<String, Any>(

                                "status" to soumission.status,

                                "submitted_at" to
                                        (soumission.submitted_at ?: ""),

                                "ao_id" to
                                        soumission.ao_id,

                                "envelopes" to
                                        (soumission.envelopes?.size ?: 0),

                                "observations" to
                                        "Aucune observation disponible"
                            )

                        _notes.value =
                            Resource.Success(notesMap)

                    } else {

                        _notes.value =
                            Resource.Error("Données introuvables")
                    }

                } else {

                    _notes.value =
                        Resource.Error("Erreur ${response.code()}")
                }

            } catch (e: Exception) {

                _notes.value =
                    Resource.Error("Connexion impossible")
            }
        }
    }
}