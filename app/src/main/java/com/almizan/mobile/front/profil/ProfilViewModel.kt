package com.almizan.mobile.front.profil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.almizan.mobile.data.api.ApiClient
import com.almizan.mobile.data.models.User
import com.almizan.mobile.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfilViewModel(application: Application) : AndroidViewModel(application) {

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val userState: StateFlow<Resource<User>> = _userState

    fun loadProfile() {
        android.util.Log.e("PROFIL", "=== loadProfile() appelé ===")  // Log.e pour être sûr de le voir

        viewModelScope.launch {
            android.util.Log.e("PROFIL", "=== dans la coroutine ===")

            _userState.value = Resource.Loading
            // Dans ProfilViewModel.kt, remplace le bloc try/catch par :
            try {
                val api = ApiClient.create(getApplication())
                val response = api.getMe()

                android.util.Log.d("PROFIL", "Code: ${response.code()}")
                android.util.Log.d("PROFIL", "Body: ${response.body()}")
                android.util.Log.d("PROFIL", "Error: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {
                    _userState.value = Resource.Success(response.body()!!)
                } else {
                    _userState.value = Resource.Error(
                        "Erreur ${response.code()} : ${response.errorBody()?.string()}",
                        response.code()
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("PROFIL", "Exception: ${e.message}")
                _userState.value = Resource.Error(e.message ?: "Erreur réseau")
            }
        }
    }
}