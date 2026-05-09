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

class MarcheDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _marche = MutableLiveData<Resource<Marche>>()
    val marche: LiveData<Resource<Marche>> = _marche

    fun loadMarche(id: String) {
        _marche.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.getMarcheById(id)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) _marche.value = Resource.Success(data)
                    else _marche.value = Resource.Error("Marché introuvable")
                } else {
                    _marche.value = Resource.Error("Erreur ${response.code()}")
                }
            } catch (e: Exception) {
                _marche.value = Resource.Error("Connexion impossible")
            }
        }
    }

    fun downloadCdc(marcheId: String) {
        viewModelScope.launch {
            try { api.downloadCdc(marcheId) } catch (_: Exception) {}
        }
    }
}