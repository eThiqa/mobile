package com.almizan.mobile.front.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.almizan.mobile.databinding.FragmentDashboardBinding
import com.almizan.mobile.front.marches.MarcheAdapter
import com.almizan.mobile.front.marches.MarcheDetailActivity
import com.almizan.mobile.utils.Resource
import com.almizan.mobile.utils.SessionManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var marchesAdapter: MarcheAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        binding.tvUserName.text = session.getUserData("prenom") ?: "Opérateur"

        setupRecyclerView()
        observeViewModel()

        viewModel.loadDashboard()

        binding.cardNouveauxMarches.setOnClickListener {
            // Navigation via Activity parent ou FragmentManager si besoin
        }
    }

    private fun setupRecyclerView() {
        marchesAdapter = MarcheAdapter { marche ->
            val intent = Intent(requireContext(), MarcheDetailActivity::class.java)
            intent.putExtra("marche_id", marche.id)
            startActivity(intent)
        }
        binding.rvRecentMarches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = marchesAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        viewModel.stats.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvMarchesOuverts.text = "…"
                    binding.tvMesSoumissions.text = "…"
                    binding.tvRecoursPossibles.text = "…"
                }
                is Resource.Success -> {
                    val stats = resource.data
                    binding.tvMarchesOuverts.text = stats["marchesOuverts"]?.toString() ?: "0"
                    binding.tvMesSoumissions.text = stats["mesSoumissions"]?.toString() ?: "0"
                    binding.tvRecoursPossibles.text = stats["recoursPossibles"]?.toString() ?: "0"
                }
                is Resource.Error -> {
                    binding.tvMarchesOuverts.text = "—"
                    binding.tvMesSoumissions.text = "—"
                    binding.tvRecoursPossibles.text = "—"
                }
            }
        }

        viewModel.recentMarches.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    marchesAdapter.submitList(resource.data)
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
