package com.almizan.mobile.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "almizan_session")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_ROLE = stringPreferencesKey("user_role")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_NOM = stringPreferencesKey("nom")
        val KEY_PRENOM = stringPreferencesKey("prenom")
        val KEY_TELEPHONE = stringPreferencesKey("telephone")
        val KEY_RAISON_SOCIALE = stringPreferencesKey("raison_sociale")
        val KEY_REGISTRE_COMMERCE = stringPreferencesKey("registre_commerce")
    }

    // Flow réactif pour ProfilFragment
    val userDataFlow: Flow<Map<String, String>>
        get() = context.dataStore.data.map { prefs ->
            mapOf(
                "nom" to (prefs[KEY_NOM] ?: ""),
                "prenom" to (prefs[KEY_PRENOM] ?: ""),
                "email" to (prefs[KEY_USER_EMAIL] ?: ""),
                "telephone" to (prefs[KEY_TELEPHONE] ?: ""),
                "raison_sociale" to (prefs[KEY_RAISON_SOCIALE] ?: ""),
                "registre_commerce" to (prefs[KEY_REGISTRE_COMMERCE] ?: "")
            )
        }

    fun getToken(): String? = runBlocking {
        context.dataStore.data.first()[KEY_TOKEN]
    }

    suspend fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()
    fun getUserRole(): String? = runBlocking {
        context.dataStore.data.first()[KEY_USER_ROLE]
    }

    fun getUserData(key: String): String? = runBlocking {
        context.dataStore.data.first()[stringPreferencesKey(key)]
    }

    suspend fun saveSession(
        token: String,
        userId: String,
        name: String,
        role: String,
        email: String,
        nom: String = "",
        prenom: String = "",
        telephone: String = "",
        raisonSociale: String = "",
        registreCommerce: String = ""
    ) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_USER_ID] = userId
            it[KEY_USER_NAME] = name
            it[KEY_USER_ROLE] = role
            it[KEY_USER_EMAIL] = email
            it[KEY_NOM] = nom
            it[KEY_PRENOM] = prenom
            it[KEY_TELEPHONE] = telephone
            it[KEY_RAISON_SOCIALE] = raisonSociale
            it[KEY_REGISTRE_COMMERCE] = registreCommerce
        }
    }

    suspend fun saveUserData(
        nom: String,
        prenom: String,
        telephone: String,
        raisonSociale: String
    ) {
        context.dataStore.edit {
            it[KEY_NOM] = nom
            it[KEY_PRENOM] = prenom
            it[KEY_TELEPHONE] = telephone
            it[KEY_RAISON_SOCIALE] = raisonSociale
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}