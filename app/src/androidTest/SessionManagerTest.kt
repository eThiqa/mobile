package com.almizan.mobile

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.almizan.mobile.utils.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        sessionManager = SessionManager(appContext)

        // Nettoyer la session avant chaque test
        runBlocking {
            sessionManager.clearSession()
        }
    }

    @Test
    fun testSaveAndRetrieveOperatorSession() = runBlocking {
        // 1. Sauvegarder une fausse session d'Opérateur Économique
        sessionManager.saveSession(
            token = "fake_jwt_token_123",
            userId = "uuid-1234",
            name = "Entreprise Test",
            role = "OE",
            email = "contact@entreprise.dz",
            nom = "Benali",
            prenom = "Omar",
            telephone = "0555001122"
        )

        // 2. Vérifier que l'utilisateur est bien connecté
        assertTrue("L'utilisateur devrait être connecté", sessionManager.isLoggedIn())

        // 3. Vérifier que les données nécessaires pour le paiement sont correctes
        val email = sessionManager.getUserData("user_email") // Doit correspondre à KEY_USER_EMAIL
        val nom = sessionManager.getUserData("nom")
        val role = sessionManager.getUserRole()

        assertEquals("L'email doit correspondre", "contact@entreprise.dz", email)
        assertEquals("Le nom doit correspondre", "Benali", nom)
        assertEquals("Le rôle doit être OE", "OE", role)
    }

    @Test
    fun testClearSession() = runBlocking {
        // Sauvegarder puis effacer
        sessionManager.saveSession("token", "id", "name", "OE", "test@test.dz")
        sessionManager.clearSession()

        assertFalse("L'utilisateur devrait être déconnecté", sessionManager.isLoggedIn())
        assertNull("Le token devrait être null", sessionManager.getToken())
    }
}