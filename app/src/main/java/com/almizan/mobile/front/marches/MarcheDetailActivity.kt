package com.almizan.mobile.front.marches

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityMarcheDetailBinding
import com.almizan.mobile.front.soumission.SoumissionActivity
import com.almizan.mobile.utils.Resource
import com.almizan.mobile.utils.SessionManager // ✅ Nouvel import ajouté
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class MarcheDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarcheDetailBinding
    private val viewModel: MarcheDetailViewModel by viewModels()
    private var marcheId: String = ""
    private var marcheTitre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarcheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        marcheId = intent.getStringExtra("marche_id") ?: ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialisation de la session
        val session = SessionManager(this)

        viewModel.loadMarche(marcheId)

        // Bouton Questions
        binding.btnQuestions.setOnClickListener {
            val intent = Intent(this, QuestionsActivity::class.java)
            intent.putExtra("marche_id", marcheId)
            intent.putExtra("marche_titre", marcheTitre)
            startActivity(intent)
        }

        // --- NOUVELLE LOGIQUE DE PAIEMENT / RETRAIT CDC ---

        // Par défaut, on cache le bouton soumettre jusqu'à ce que le CDC soit payé
        binding.btnSoumettre.visibility = View.GONE

        // Bouton Télécharger CDC devient "Payer et Retirer"
        binding.btnDownloadCdc.text = "💳 Payer et retirer le CDC"
        binding.btnDownloadCdc.setOnClickListener {
            val nom = session.getUserData("nom") ?: "Nom"
            val prenom = session.getUserData("prenom") ?: "Prénom"
            val email = session.getUserData("user_email") ?: "email@test.com"

            viewModel.payerEtRetirerCdc(marcheId, "$prenom $nom", email)
        }

        // Submit offer button
        binding.btnSoumettre.setOnClickListener {
            val intent = Intent(this, SoumissionActivity::class.java)
            intent.putExtra("marche_id", marcheId)
            intent.putExtra("marche_titre", marcheTitre)
            startActivity(intent)
        }

        // Ajouter l'observation du paiement
        viewModel.withdrawalState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnDownloadCdc.isEnabled = false
                    binding.btnDownloadCdc.text = "⏳ Traitement du paiement..."
                }
                is Resource.Success -> {
                    binding.btnDownloadCdc.isEnabled = false
                    binding.btnDownloadCdc.text = "✅ CDC Payé et Retiré"
                    binding.btnSoumettre.visibility = View.VISIBLE // 🔓 Débloque la soumission
                    Snackbar.make(binding.root, "Paiement CIB validé avec succès !", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    binding.btnDownloadCdc.isEnabled = true
                    binding.btnDownloadCdc.text = "💳 Réessayer le paiement"
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        // --- OBSERVATION DES DÉTAILS DU MARCHÉ ---

        viewModel.marche.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // optionally show progress
                }
                is Resource.Success -> {
                    val m = resource.data
                    marcheTitre = m.getTitre()
                    supportActionBar?.title = m.getReference()
                    binding.tvTitre.text = m.getTitre()
                    binding.tvReference.text = m.getReference()
                    binding.tvServiceContractant.text = m.service_contractant_id
                    binding.tvWilaya.text = ""
                    binding.tvType.text = m.getTypeMarche().replace("_", " ")
                    binding.tvMode.text = m.getModePassation().replace("_", " ")
                    binding.tvSecteur.text = m.version_courante?.nature_projet ?: ""
                    binding.tvDatePublication.text = m.created_at.take(10)
                    binding.tvDateRetrait.text = m.getDateLimite().take(10)
                    binding.tvDateSoumission.text = m.getDateLimite().take(10)

                    m.getBudget()?.let { budget ->
                        val fmt = NumberFormat.getNumberInstance(Locale.FRANCE)
                        binding.tvBudget.text = "${fmt.format(budget)} DA"
                        binding.rowBudget.visibility = View.VISIBLE
                    }

                    // On vérifie seulement s'il y a un CDC à la base, mais on préserve
                    // le texte "✅ CDC Payé et Retiré" s'il a déjà été payé durant la session
                    if (binding.btnDownloadCdc.text != "✅ CDC Payé et Retiré") {
                        binding.btnDownloadCdc.isEnabled = m.version_courante?.cdc_contenu != null
                    }
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, (resource as Resource.Error).message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}