package com.almizan.mobile.front.soumission


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.almizan.mobile.databinding.FragmentEnveloppeAdminBinding
import com.google.android.material.chip.Chip

class EnveloppeAdminFragment : Fragment() {

    private var _binding: FragmentEnveloppeAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoumissionViewModel by activityViewModels()

    companion object {
        fun newInstance(marcheId: String) = EnveloppeAdminFragment().apply {
            arguments = Bundle().apply { putString("marche_id", marcheId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnveloppeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAjouterDoc.setOnClickListener {
            (activity as? SoumissionActivity)?.pickFile("admin")
        }

        binding.btnScanDoc.setOnClickListener {
            (activity as? SoumissionActivity)?.launchScan("admin")
        }

        viewModel.documentsAdmin.observe(viewLifecycleOwner) { docs ->
            binding.chipGroupDocs.removeAllViews()
            docs.forEachIndexed { _, uri ->
                val chip = Chip(requireContext()).apply {
                    text = uri.lastPathSegment ?: "Document"
                    isCloseIconVisible = true
                    setOnCloseIconClickListener { viewModel.removeAdminDocument(uri) }
                }
                binding.chipGroupDocs.addView(chip)
            }
            binding.tvNbDocs.text = "${docs.size} document(s) ajouté(s)"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}