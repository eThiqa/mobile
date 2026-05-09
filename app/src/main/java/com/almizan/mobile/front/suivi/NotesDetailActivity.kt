package com.almizan.mobile.front.suivi


import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityNotesDetailBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class NotesDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesDetailBinding
    private val viewModel: NotesDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val soumissionId = intent.getStringExtra("soumission_id") ?: ""
        val marcheTitre = intent.getStringExtra("marche_titre") ?: ""

        supportActionBar?.title = "Détail des notes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvMarcheTitre.text = marcheTitre

        viewModel.notes.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val notes = resource.data
                    binding.tvNoteAdmin.text =
                        "%.2f / 100".format(notes["noteAdministrative"] as? Double ?: 0.0)
                    binding.tvNoteTechnique.text =
                        "%.2f / 100".format(notes["noteTechnique"] as? Double ?: 0.0)
                    binding.tvNoteFinanciere.text =
                        "%.2f / 100".format(notes["noteFinanciere"] as? Double ?: 0.0)
                    binding.tvNoteGlobale.text =
                        "%.2f / 100".format(notes["noteGlobale"] as? Double ?: 0.0)
                    val rang = (notes["rang"] as? Double)?.toInt()
                    binding.tvRang.text = if (rang != null) "Classement : #$rang" else "—"
                    binding.tvObservations.text =
                        notes["observations"] as? String ?: "Aucune observation"
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loadNotes(soumissionId)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}