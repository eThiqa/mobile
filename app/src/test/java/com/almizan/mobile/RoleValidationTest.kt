package com.almizan.mobile

import org.junit.Assert.*
import org.junit.Test

class RoleValidationTest {

    // Fonction qui imite la logique de LoginViewModel
    private fun isUserAllowedInMobileApp(role: String?): Boolean {
        if (role == null) return false
        val normalizedRole = role.trim().uppercase()
        return normalizedRole == "OE" || normalizedRole == "OPERATEUR"
    }

    @Test
    fun `test operator roles are allowed`() {
        assertTrue("Role OE devrait être autorisé", isUserAllowedInMobileApp("OE"))
        assertTrue("Role OPERATEUR devrait être autorisé", isUserAllowedInMobileApp("OPERATEUR"))
        assertTrue("Role en minuscules devrait être autorisé", isUserAllowedInMobileApp("oe"))
    }

    @Test
    fun `test admin and other roles are rejected`() {
        assertFalse("Role ADM devrait être rejeté", isUserAllowedInMobileApp("ADM"))
        assertFalse("Role SC devrait être rejeté", isUserAllowedInMobileApp("SC"))
        assertFalse("Role CIM_MEMBER devrait être rejeté", isUserAllowedInMobileApp("CIM_MEMBER"))
        assertFalse("Role null devrait être rejeté", isUserAllowedInMobileApp(null))
        assertFalse("Role vide devrait être rejeté", isUserAllowedInMobileApp(""))
    }
}