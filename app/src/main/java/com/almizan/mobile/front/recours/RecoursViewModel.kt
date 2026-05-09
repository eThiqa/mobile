package com.almizan.mobile.front.recours

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Recours
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class RecoursViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)
    private var motif: String = ""

    private val _recoursState = MutableLiveData<Resource<Boolean>>()
    val recoursState: LiveData<Resource<Boolean>> = _recoursState

    fun setMotif(motif: String) {
        this.motif = motif
    }

    fun deposerRecours(soumissionId: String, contenu: String, pieceJointe: Uri?) {
        if (motif.isBlank()) {
            _recoursState.value = Resource.Error("Veuillez sélectionner un motif")
            return
        }
        if (contenu.length < 50) {
            _recoursState.value = Resource.Error("Contenu trop court (50 caractères minimum)")
            return
        }

        _recoursState.value = Resource.Loading
        viewModelScope.launch {
            try {
                val recours = Recours(
                    id = "",
                    soumissionId = soumissionId,
                    motif = motif,
                    contenu = contenu,
                    pieceJointe = pieceJointe?.toString(),
                    dateDepot = null
                )
                val response = api.deposerRecours(recours)
                if (response.isSuccessful) {
                    _recoursState.value = Resource.Success(true)
                } else {
                    val msg = when (response.code()) {
                        403 -> "Délai de recours dépassé (10 jours)"
                        409 -> "Un recours a déjà été déposé pour ce marché"
                        else -> "Erreur ${response.code()}"
                    }
                    _recoursState.value = Resource.Error(msg)
                }
            } catch (e: Exception) {
                _recoursState.value = Resource.Error("Connexion impossible")
            }
        }
    }
}