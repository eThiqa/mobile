package com.almizan.mobile.front.soumission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almizan.mobile.databinding.ActivitySoumissionBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class SoumissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySoumissionBinding
    private val viewModel: SoumissionViewModel by viewModels()
    private var marcheId: String = ""

    // File pickers
    private var currentPickerTarget = ""
    private val filePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addDocument(currentPickerTarget, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoumissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        marcheId = intent.getStringExtra("marche_id") ?: ""
        viewModel.setMarcheId(marcheId)

        supportActionBar?.title = "Déposer une offre"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup ViewPager avec 3 onglets
        val adapter = SoumissionPagerAdapter(this, marcheId)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Navigation contrôlée

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "1. Admin"
                1 -> "2. Technique"
                2 -> "3. Financier"
                else -> ""
            }
        }.attach()

        // Bouton suivant
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < 2) {
                binding.viewPager.currentItem = current + 1
                if (current == 1) binding.btnNext.text = "Confirmer le dépôt"
            } else {
                confirmerSoumission()
            }
        }

        binding.btnPrev.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
                binding.btnNext.text = "Suivant"
            }
        }

        viewModel.soumissionState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnNext.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, "✅ Offre déposée avec succès !", Snackbar.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnNext.isEnabled = true
                    Snackbar.make(binding.root, "❌ ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmerSoumission() {
        viewModel.confirmerSoumission()
    }

    fun pickFile(target: String) {
        currentPickerTarget = target
        filePicker.launch("application/pdf")
    }

    fun launchScan(target: String) {
        currentPickerTarget = target
        val intent = Intent(this, ScanDocumentActivity::class.java)
        startActivity(intent)
    }
}