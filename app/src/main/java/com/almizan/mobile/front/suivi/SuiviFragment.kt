package com.almizan.mobile.front.suivi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.almizan.mobile.databinding.FragmentSuiviBinding
import com.almizan.mobile.front.recours.RecoursActivity
import com.almizan.mobile.front.soumission.SoumissionAdapter
import com.almizan.mobile.utils.Resource

class SuiviFragment : Fragment() {

    private var _binding: FragmentSuiviBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SuiviViewModel by viewModels()
    private lateinit var adapter: SoumissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuiviBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SoumissionAdapter(
            onVoirNotes = { soumission ->
                val intent = Intent(requireContext(), NotesDetailActivity::class.java)
                intent.putExtra("soumission_id", soumission.id)
                intent.putExtra("marche_titre", soumission.marcheTitre)
                startActivity(intent)
            },
            onDeposerRecours = { soumission ->
                val intent = Intent(requireContext(), RecoursActivity::class.java)
                intent.putExtra("soumission_id", soumission.id)
                intent.putExtra("marche_titre", soumission.marcheTitre)
                startActivity(intent)
            }
        )

        binding.rvSoumissions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSoumissions.adapter = adapter

        viewModel.soumissions.observe(viewLifecycleOwner) { resource ->
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
                    binding.tvEmpty.text = resource.message
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }

        viewModel.loadSoumissions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSoumissions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}