package com.almizan.mobile.front.soumission

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Soumission
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class SoumissionViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)
    private var marcheId: String = ""

    // Documents par enveloppe
    val documentsAdmin = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val documentTechnique = MutableLiveData<Uri?>()
    val documentFinancier = MutableLiveData<Uri?>()
    val montantOffre = MutableLiveData<String>("")

    private val _soumissionState = MutableLiveData<Resource<Soumission>>()
    val soumissionState: LiveData<Resource<Soumission>> = _soumissionState

    fun setMarcheId(id: String) { marcheId = id }

    fun addDocument(target: String, uri: Uri) {
        when (target) {
            "admin" -> {
                val list = documentsAdmin.value ?: mutableListOf()
                list.add(uri)
                documentsAdmin.value = list
            }
            "technique" -> documentTechnique.value = uri
            "financier" -> documentFinancier.value = uri
        }
    }

    fun removeAdminDocument(uri: Uri) {
        val list = documentsAdmin.value ?: mutableListOf()
        list.remove(uri)
        documentsAdmin.value = list
    }
    fun confirmerSoumission() {
        if (marcheId.isBlank()) {
            _soumissionState.value = Resource.Error("Marché non identifié")
            return
        }
        val montant = montantOffre.value?.toDoubleOrNull()
        if (montant == null || montant <= 0) {
            _soumissionState.value = Resource.Error("Montant de l'offre financière invalide")
            return
        }

        _soumissionState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val docFinancier = documentFinancier.value
                if (docFinancier != null) {
                    val bytes = ByteArray(0)
                    val reqBody = okhttp3.RequestBody.create(
                        "application/pdf".toMediaTypeOrNull(), bytes
                    )
                    val finPart = MultipartBody.Part.createFormData(
                        "file", "offre_financiere.pdf", reqBody
                    )
                    val montantBody = okhttp3.RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), montant.toString()
                    )
                    api.uploadEnveloppeFinanciere(marcheId, finPart, montantBody)
                }

                val response = api.confirmerSoumission(marcheId)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) _soumissionState.value = Resource.Success(data)
                    else _soumissionState.value = Resource.Error("Réponse invalide")
                } else {
                    val msg = when (response.code()) {
                        400 -> "Dossier incomplet"
                        403 -> "Délai de soumission dépassé"
                        409 -> "Une offre a déjà été déposée pour ce marché"
                        else -> "Erreur ${response.code()}"
                    }
                    _soumissionState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _soumissionState.value = Resource.Error("Connexion impossible")
            }
        }
    }
  }