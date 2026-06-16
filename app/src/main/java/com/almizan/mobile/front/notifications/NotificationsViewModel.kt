package com.almizan.mobile.front.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.Notification
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.launch

class NotificationsViewModel(app: Application) : AndroidViewModel(app) {

    private val api = ApiClient.create(app)

    private val _notifications = MutableLiveData<Resource<List<Notification>>>()
    val notifications: LiveData<Resource<List<Notification>>> = _notifications

    fun loadNotifications() {
        _notifications.value = Resource.Loading
        viewModelScope.launch {
            try {
                val response = api.getNotifications()
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    _notifications.value = Resource.Success(list)
                } else {
                    _notifications.value = Resource.Error("Erreur lors du chargement des notifications")
                }
            } catch (e: Exception) {
                _notifications.value = Resource.Error("Connexion impossible")
            }
        }
    }

    fun marquerCommeLue(id: String) {
        viewModelScope.launch {
            try {
                val response = api.marquerLue(id)
                if (response.isSuccessful) {
                    loadNotifications() // Recharger la liste pour mettre à jour l'UI
                }
            } catch (e: Exception) {
                // Ignore silent fail
            }
        }
    }

    fun marquerToutesLues() {
        _notifications.value = Resource.Loading
        viewModelScope.launch {
            try {
                api.marquerToutesLues()
                loadNotifications()
            } catch (e: Exception) {
                _notifications.value = Resource.Error("Impossible de marquer comme lu")
            }
        }
    }
}