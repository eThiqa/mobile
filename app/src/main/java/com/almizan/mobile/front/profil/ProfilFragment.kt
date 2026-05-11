package com.almizan.mobile.front.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.almizan.mobile.data.models.User
import com.almizan.mobile.databinding.FragmentProfilBinding
import com.almizan.mobile.front.auth.LoginActivity
import com.almizan.mobile.utils.Resource
import com.almizan.mobile.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private val viewModel: ProfilViewModel by viewModels()
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

        viewModel.loadProfile()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        afficherUser(state.data)
                        session.saveUserData(
                            nom = state.data.last_name,
                            prenom = state.data.first_name,
                            telephone = state.data.phone_number ?: "",
                            raisonSociale = ""
                        )
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        chargerDepuisDataStore()
                        Snackbar.make(binding.root, "Données locales (${state.message})", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnModifier.setOnClickListener {
            if (!isEditMode) activerEdition()
            else binding.btnSauvegarder.performClick()
        }

        binding.btnSauvegarder.setOnClickListener {
            sauvegarderProfil()
        }

        binding.btnDeconnexion.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Se déconnecter") { _, _ ->
                    lifecycleScope.launch {
                        session.clearSession()
                        naviguerVersLogin()
                    }
                }
                .show()
        }
    }

    private fun afficherUser(user: User) {
        binding.tvNomComplet.text = "${user.first_name} ${user.last_name}".trim().ifEmpty { "Mon Profil" }
        binding.tvEmail.text = user.email
        binding.tvAvatar.text = buildString {
            if (user.first_name.isNotEmpty()) append(user.first_name.first().uppercaseChar())
            if (user.last_name.isNotEmpty()) append(user.last_name.first().uppercaseChar())
        }.ifEmpty { "?" }
        binding.etNom.setText(user.last_name)
        binding.etPrenom.setText(user.first_name)
        binding.etTelephone.setText(user.phone_number ?: "")
        binding.etWilaya.setText(user.wilaya ?: "")
        binding.etAdresse.setText(user.address ?: "")
        binding.tvStatut.text = if (user.is_verified) "✅ Vérifié" else "⏳ En attente de vérification"
        binding.tvRole.text = when (user.role) {
            "OPERATEUR", "OE" -> "Opérateur Économique"
            "ADM", "ADMIN" -> "Administrateur"
            "ACHETEUR", "SC" -> "Acheteur Public"
            else -> user.role
        }
    }

    private fun activerEdition() {
        isEditMode = true
        listOf(binding.etNom, binding.etPrenom, binding.etTelephone,
            binding.etWilaya, binding.etAdresse).forEach {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.isEnabled = true
        }
        binding.etNom.requestFocus()
        binding.btnModifier.text = "✏️ En cours d'édition…"
        binding.btnSauvegarder.visibility = View.VISIBLE
    }

    private fun desactiverEdition() {
        isEditMode = false
        listOf(binding.etNom, binding.etPrenom, binding.etTelephone,
            binding.etWilaya, binding.etAdresse).forEach {
            it.isFocusable = false
            it.isFocusableInTouchMode = false
            it.isEnabled = false
        }
        binding.btnModifier.text = "✏️ Modifier le profil"
        binding.btnSauvegarder.visibility = View.GONE
    }

    private fun sauvegarderProfil() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSauvegarder.isEnabled = false
        lifecycleScope.launch {
            try {
                session.saveUserData(
                    nom = binding.etNom.text.toString().trim(),
                    prenom = binding.etPrenom.text.toString().trim(),
                    telephone = binding.etTelephone.text.toString().trim(),
                    raisonSociale = ""
                )
                binding.progressBar.visibility = View.GONE
                binding.btnSauvegarder.isEnabled = true
                desactiverEdition()
                Snackbar.make(binding.root, "✅ Profil mis à jour", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnSauvegarder.isEnabled = true
                Snackbar.make(binding.root, "Erreur lors de la sauvegarde", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun chargerDepuisDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            session.userDataFlow.collectLatest { data ->
                val prenom = data["prenom"] ?: ""
                val nom = data["nom"] ?: ""
                binding.etNom.setText(nom)
                binding.etPrenom.setText(prenom)
                binding.etTelephone.setText(data["telephone"] ?: "")
                binding.tvNomComplet.text = "$prenom $nom".trim().ifEmpty { "Mon Profil" }
                binding.tvEmail.text = data["email"] ?: ""
                binding.tvAvatar.text = buildString {
                    if (prenom.isNotEmpty()) append(prenom.first().uppercaseChar())
                    if (nom.isNotEmpty()) append(nom.first().uppercaseChar())
                }.ifEmpty { "?" }
            }
        }
    }

    private fun naviguerVersLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}