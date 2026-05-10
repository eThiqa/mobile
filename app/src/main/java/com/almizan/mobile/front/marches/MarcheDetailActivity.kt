package com.almizan.mobile.front.marches

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityMarcheDetailBinding
import com.almizan.mobile.front.soumission.SoumissionActivity
import com.almizan.mobile.utils.Resource
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

        viewModel.loadMarche(marcheId)

        // Questions button
        binding.btnQuestions.setOnClickListener {
            val intent = Intent(this, QuestionsActivity::class.java)
            intent.putExtra("marche_id", marcheId)
            intent.putExtra("marche_titre", marcheTitre)
            startActivity(intent)
        }

        // Download CDC button
        binding.btnDownloadCdc.setOnClickListener {
            Snackbar.make(binding.root, "Téléchargement du CDC...", Snackbar.LENGTH_SHORT).show()
            // TODO: open CDC URL in browser or download
        }

        // Submit offer button
        binding.btnSoumettre.setOnClickListener {
            val intent = Intent(this, SoumissionActivity::class.java)
            intent.putExtra("marche_id", marcheId)
            intent.putExtra("marche_titre", marcheTitre)
            startActivity(intent)
        }

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

                    binding.btnDownloadCdc.isEnabled = m.version_courante?.cdc_contenu != null
                    binding.btnSoumettre.visibility =
                        if (m.status == "PUBLISHED") View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, (resource as Resource.Error).message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}