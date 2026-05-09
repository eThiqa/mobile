package com.almizan.mobile.front.marches



import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityMarcheDetailBinding
import com.almizan.mobile.front.marches.MarcheDetailViewModel
import com.almizan.mobile.front.soumission.SoumissionActivity
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class MarcheDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarcheDetailBinding
    private val viewModel: MarcheDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarcheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val marcheId = intent.getStringExtra("marche_id") ?: ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.loadMarche(marcheId)

        viewModel.marche.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val m = resource.data
                    supportActionBar?.title = m.reference

                    binding.tvTitre.text = m.titre
                    binding.tvReference.text = m.reference
                    binding.tvServiceContractant.text = m.serviceContractant
                    binding.tvWilaya.text = "📍 ${m.wilaya}"
                    binding.tvType.text = m.typeMarche
                    binding.tvMode.text = m.modePassation.replace("_", " ")
                    binding.tvSecteur.text = m.secteurActivite

                    binding.tvDatePublication.text = m.datePublication
                    binding.tvDateRetrait.text = m.dateLimiteRetrait
                    binding.tvDateSoumission.text = m.dateLimiteSoumission

                    m.budgetEstimatif?.let { budget ->
                        val fmt = NumberFormat.getNumberInstance(Locale.FRANCE)
                        binding.tvBudget.text = "${fmt.format(budget)} DA"
                        binding.rowBudget.visibility = View.VISIBLE
                    }

                    // Bouton télécharger CDC
                    binding.btnDownloadCdc.isEnabled = m.cdcDisponible
                    binding.btnDownloadCdc.setOnClickListener {
                        viewModel.downloadCdc(m.id)
                        Snackbar.make(binding.root, "Téléchargement du CDC en cours...", Snackbar.LENGTH_SHORT).show()
                    }

                    // Bouton questions
                    binding.btnQuestions.setOnClickListener {
                        val intent = Intent(this, QuestionsActivity::class.java)
                        intent.putExtra("marche_id", m.id)
                        intent.putExtra("marche_titre", m.titre)
                        startActivity(intent)
                    }

                    // Bouton soumettre
                    binding.btnSoumettre.visibility =
                        if (m.statut.name == "EN_COURS") View.VISIBLE else View.GONE
                    binding.btnSoumettre.setOnClickListener {
                        val intent = Intent(this, SoumissionActivity::class.java)
                        intent.putExtra("marche_id", m.id)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}