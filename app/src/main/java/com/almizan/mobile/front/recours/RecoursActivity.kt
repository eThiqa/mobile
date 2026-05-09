package com.almizan.mobile.front.recours

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivityRecoursBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class RecoursActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoursBinding
    private val viewModel: RecoursViewModel by viewModels()
    private var pieceJointeUri: Uri? = null

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pieceJointeUri = it
            binding.tvPieceJointe.text = "📎 Document joint"
            binding.tvPieceJointe.setTextColor(getColor(com.almizan.mobile.R.color.primary_green))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoursBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val soumissionId = intent.getStringExtra("soumission_id") ?: ""
        val marcheTitre = intent.getStringExtra("marche_titre") ?: ""

        supportActionBar?.title = "Déposer un recours"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvMarcheTitre.text = marcheTitre
        binding.tvDelaiInfo.text = "⏱ Vous avez 10 jours après la publication de l'attribution provisoire pour déposer un recours."

        // Motifs de recours
        binding.rgMotif.setOnCheckedChangeListener { _, id ->
            val motif = when (id) {
                binding.rbIrregularite.id -> "IRREGULARITE_PROCEDURE"
                binding.rbFavoritisme.id -> "FAVORITISME"
                binding.rbCriteres.id -> "CRITERES_NON_RESPECTES"
                binding.rbAutre.id -> "AUTRE"
                else -> ""
            }
            viewModel.setMotif(motif)
        }

        binding.btnJoindre.setOnClickListener {
            filePicker.launch("application/pdf")
        }

        binding.btnSoumettre.setOnClickListener {
            val contenu = binding.etContenu.text.toString().trim()
            if (contenu.length < 50) {
                binding.etContenu.error = "Détaillez votre recours (50 caractères minimum)"
                return@setOnClickListener
            }
            viewModel.deposerRecours(soumissionId, contenu, pieceJointeUri)
        }

        viewModel.recoursState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSoumettre.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, "✅ Recours déposé avec succès", Snackbar.LENGTH_LONG)
                        .addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(snackbar: Snackbar, event: Int) { finish() }
                        }).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSoumettre.isEnabled = true
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}