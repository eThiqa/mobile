package com.almizan.mobile.front.soumission


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.almizan.mobile.databinding.FragmentEnveloppeTechniqueBinding

class EnveloppeTechniqueFragment : Fragment() {

    private var _binding: FragmentEnveloppeTechniqueBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoumissionViewModel by activityViewModels()

    companion object {
        fun newInstance(marcheId: String) = EnveloppeTechniqueFragment().apply {
            arguments = Bundle().apply { putString("marche_id", marcheId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnveloppeTechniqueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUploadTechnique.setOnClickListener {
            (activity as? SoumissionActivity)?.pickFile("technique")
        }

        viewModel.documentTechnique.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.tvDocTechniqueNom.text = "✅ ${uri.lastPathSegment ?: "Mémoire technique"}"
                binding.tvDocTechniqueNom.setTextColor(
                    requireContext().getColor(com.almizan.mobile.R.color.status_green)
                )
                binding.cardWarning.visibility = View.GONE
            } else {
                binding.tvDocTechniqueNom.text = "Aucun document sélectionné"
                binding.tvDocTechniqueNom.setTextColor(
                    requireContext().getColor(com.almizan.mobile.R.color.text_secondary)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}