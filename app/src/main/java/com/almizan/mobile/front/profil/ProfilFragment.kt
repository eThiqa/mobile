package com.almizan.mobile.front.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.almizan.mobile.databinding.FragmentProfilBinding
import com.almizan.mobile.front.auth.LoginActivity
import com.almizan.mobile.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        chargerProfil()

        // Bouton Modifier / Sauvegarder
        binding.btnModifier.setOnClickListener {
            if (!isEditMode) activerEdition()
            else binding.btnSauvegarder.performClick()
        }

        binding.btnSauvegarder.setOnClickListener {
            sauvegarderProfil()
        }

        // Bouton Déconnexion
        binding.btnDeconnexion.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Se déconnecter") { _, _ ->
                    deconnecter()
                }
                .show()
        }
    }

    private fun chargerProfil() {
        // Charger depuis SessionManager
        val nom = session.getUserData("nom") ?: ""
        val prenom = session.getUserData("prenom") ?: ""
        val email = session.getUserData("email") ?: ""
        val telephone = session.getUserData("telephone") ?: ""
        val raisonSociale = session.getUserData("raison_sociale") ?: ""
        val registreCommerce = session.getUserData("registre_commerce") ?: ""

        binding.etNom.setText(nom)
        binding.etPrenom.setText(prenom)
        binding.etTelephone.setText(telephone)
        binding.etRaisonSociale.setText(raisonSociale)
        binding.etRegistreCommerce.setText(registreCommerce)
        binding.tvEmail.text = email
        binding.tvNomComplet.text = "$prenom $nom".trim().ifEmpty { "Mon Profil" }

        // Initiales avatar
        val initiales = buildString {
            if (prenom.isNotEmpty()) append(prenom.first().uppercaseChar())
            if (nom.isNotEmpty()) append(nom.first().uppercaseChar())
        }.ifEmpty { "OE" }
        binding.tvAvatar.text = initiales
    }

    private fun activerEdition() {
        isEditMode = true
        binding.etNom.isEnabled = true
        binding.etPrenom.isEnabled = true
        binding.etTelephone.isEnabled = true
        binding.etRaisonSociale.isEnabled = true
        binding.btnModifier.text = "✏️ En cours d'édition…"
        binding.btnSauvegarder.visibility = View.VISIBLE
    }

    private fun desactiverEdition() {
        isEditMode = false
        binding.etNom.isEnabled = false
        binding.etPrenom.isEnabled = false
        binding.etTelephone.isEnabled = false
        binding.etRaisonSociale.isEnabled = false
        binding.btnModifier.text = "✏️ Modifier le profil"
        binding.btnSauvegarder.visibility = View.GONE
    }

    private fun sauvegarderProfil() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSauvegarder.isEnabled = false

        // TODO : appel API PATCH /profil avec les nouvelles valeurs
        // Pour l'instant on sauvegarde localement dans SessionManager
        lifecycleScope.launch {
            try {
                session.saveUserData(
                    nom = binding.etNom.text.toString().trim(),
                    prenom = binding.etPrenom.text.toString().trim(),
                    telephone = binding.etTelephone.text.toString().trim(),
                    raisonSociale = binding.etRaisonSociale.text.toString().trim()
                )
                binding.progressBar.visibility = View.GONE
                binding.btnSauvegarder.isEnabled = true
                desactiverEdition()
                chargerProfil()
                Snackbar.make(binding.root, "✅ Profil mis à jour", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnSauvegarder.isEnabled = true
                Snackbar.make(binding.root, "Erreur lors de la sauvegarde", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun deconnecter() {
        lifecycleScope.launch {
            session.clearSession()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}