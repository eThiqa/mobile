package com.almizan.mobile.front.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityRegisterBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSecteurDropdown()
        setupListeners()
        observeViewModel()
    }

    private fun setupSecteurDropdown() {
        val secteurs = listOf(
            "Travaux publics", "Fournitures", "Services",
            "Informatique & Télécoms", "BTP & Construction",
            "Santé & Pharmacie", "Agriculture & Agroalimentaire",
            "Énergie & Environnement", "Transport & Logistique",
            "Industrie & Manufacture", "Éducation & Formation", "Autre"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            secteurs
        )
        binding.actvSecteur.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            viewModel.register(
                nom = binding.etNom.text.toString().trim(),
                prenom = binding.etPrenom.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                telephone = binding.etTelephone.text.toString().trim(),
                raisonSociale = binding.etRaisonSociale.text.toString().trim(),
                registreCommerce = binding.etRegistreCommerce.text.toString().trim(),
                secteurActivite = binding.actvSecteur.text.toString().trim(),
                password = binding.etPassword.text.toString()
            )
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etNom.text.isNullOrBlank()) {
            binding.etNom.error = "Champ requis"
            isValid = false
        }
        if (binding.etPrenom.text.isNullOrBlank()) {
            binding.etPrenom.error = "Champ requis"
            isValid = false
        }
        if (binding.etEmail.text.isNullOrBlank()) {
            binding.etEmail.error = "Champ requis"
            isValid = false
        }
        if (binding.etTelephone.text.isNullOrBlank()) {
            binding.etTelephone.error = "Champ requis"
            isValid = false
        }
        if (binding.etRaisonSociale.text.isNullOrBlank()) {
            binding.etRaisonSociale.error = "Champ requis"
            isValid = false
        }
        if (binding.etRegistreCommerce.text.isNullOrBlank()) {
            binding.etRegistreCommerce.error = "Champ requis"
            isValid = false
        }
        if (binding.actvSecteur.text.isNullOrBlank()) {
            binding.actvSecteur.error = "Veuillez sélectionner un secteur"
            isValid = false
        }

        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        if (password.length < 8) {
            binding.etPassword.error = "8 caractères minimum"
            isValid = false
        }
        if (password != confirm) {
            binding.etConfirmPassword.error = "Les mots de passe ne correspondent pas"
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    Snackbar.make(
                        binding.root,
                        "✅ Compte créé ! En attente de validation par l'administrateur.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    // Retour vers Login après 2s
                    binding.root.postDelayed({
                        startActivity(Intent(this, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }, 2000)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !show
    }
}