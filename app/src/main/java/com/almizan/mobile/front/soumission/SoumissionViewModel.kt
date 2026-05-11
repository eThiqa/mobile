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

        _soumissionState.value = Resource.Loading
        viewModelScope.launch {
            try {
                // Step 1: créer le brouillon
                val createResponse = api.creerSoumission(mapOf("aoId" to marcheId))
                if (!createResponse.isSuccessful) {
                    val msg = when (createResponse.code()) {
                        409 -> "Une offre a déjà été déposée pour ce marché"
                        403 -> "Délai de soumission dépassé"
                        else -> "Erreur création ${createResponse.code()}"
                    }
                    _soumissionState.value = Resource.Error(msg)
                    return@launch
                }

                // ✅ Fix ligne 71 : body() = ApiResponse<Soumission>, extraire .data?.id
                val soumissionId = createResponse.body()?.data?.id
                if (soumissionId.isNullOrBlank()) {
                    _soumissionState.value = Resource.Error("Réponse invalide")
                    return@launch
                }

                // Step 2: upload pièces admin (bytes vides en attendant la vraie lecture)
                documentsAdmin.value?.forEachIndexed { index, _ ->
                    val bytes = ByteArray(0)
                    val reqBody = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData(
                        "file", "admin_doc_$index.pdf", reqBody
                    )
                    api.uploadAdminAttachment(soumissionId, part)
                }

                // Step 3: soumettre
                val submitResponse = api.soumettreSoumission(soumissionId)
                if (submitResponse.isSuccessful) {
                    // ✅ Fix ligne 92 : body() = ApiResponse<Soumission>, extraire .data
                    val soumission = submitResponse.body()?.data
                    if (soumission != null) {
                        _soumissionState.value = Resource.Success(soumission)
                    } else {
                        _soumissionState.value = Resource.Error("Réponse invalide")
                    }
                } else {
                    val msg = when (submitResponse.code()) {
                        400 -> "Dossier incomplet — vérifiez les enveloppes"
                        403 -> "Délai de soumission dépassé"
                        409 -> "Une offre a déjà été déposée"
                        else -> "Erreur ${submitResponse.code()}"
                    }
                    _soumissionState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _soumissionState.value = Resource.Error("Connexion impossible")
            }
        }
    }
}