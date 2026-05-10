package com.almizan.mobile.front.marches

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.almizan.mobile.databinding.FragmentMarchesBinding
import com.almizan.mobile.utils.Resource

class MarchesFragment : Fragment() {

    private var _binding: FragmentMarchesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarchesViewModel by viewModels()
    private lateinit var adapter: MarcheAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMarchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MarcheAdapter { marche ->
            val intent = Intent(requireContext(), MarcheDetailActivity::class.java)
            intent.putExtra("marche_id", marche.id)
            startActivity(intent)
        }

        binding.rvMarches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMarches.adapter = adapter

        // Live search
        binding.etSearch.addTextChangedListener {
            viewModel.search(it.toString())
        }

        // Chip filters — use real backend status values, filter client-side
        // Chip filters — use real backend status values
        binding.chipAll.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.filterByStatut(null)
        }

        binding.chipEnCours.setOnCheckedChangeListener { _, checked ->
            // Published/open tenders
            if (checked) viewModel.filterByStatut("PUBLISHED")
        }

        binding.chipCloture.setOnCheckedChangeListener { _, checked ->
            // Closed tenders
            if (checked) viewModel.filterByStatut("CLOSED")
        }

        binding.chipEvaluation.setOnCheckedChangeListener { _, checked ->
            // Evaluation phase
            if (checked) viewModel.filterByStatut("UNDER_REVIEW")
        }
        viewModel.marches.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val list = resource.data
                    adapter.submitList(list)
                    binding.tvEmpty.visibility =
                        if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = resource.message
                }
            }
        }

        viewModel.loadMarches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}