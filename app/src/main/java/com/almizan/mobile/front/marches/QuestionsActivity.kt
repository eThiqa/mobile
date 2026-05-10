package com.almizan.mobile.front.marches


import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.almizan.mobile.databinding.ActivityQuestionsBinding
import com.almizan.mobile.utils.Resource
import com.google.android.material.snackbar.Snackbar

class QuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsBinding
    private val viewModel: QuestionsViewModel by viewModels()
    private lateinit var adapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val marcheId = intent.getStringExtra("marche_id") ?: ""
        val marcheTitre = intent.getStringExtra("marche_titre") ?: ""

        supportActionBar?.title = "Questions / Éclaircissements"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvMarcheTitre.text = marcheTitre

        adapter = QuestionAdapter()
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = adapter

        binding.btnPoserQuestion.setOnClickListener {
            val contenu = binding.etQuestion.text.toString().trim()
            if (contenu.length < 10) {
                binding.etQuestion.error = "Question trop courte"
                return@setOnClickListener
            }
            viewModel.poserQuestion(marcheId, contenu)
        }

        viewModel.questions.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = resource.data
                    adapter.submitList(list)
                    binding.tvEmpty.visibility =
                        if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.postState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> binding.btnPoserQuestion.isEnabled = false
                is Resource.Success -> {
                    binding.btnPoserQuestion.isEnabled = true
                    Snackbar.make(binding.root, "✅ Question envoyée", Snackbar.LENGTH_SHORT).show()
                    viewModel.loadQuestions(marcheId)
                }
                is Resource.Error -> {
                    binding.btnPoserQuestion.isEnabled = true
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loadQuestions(marcheId)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}