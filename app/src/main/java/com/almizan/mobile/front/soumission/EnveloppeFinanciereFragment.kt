package com.almizan.mobile.front.soumission


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.almizan.mobile.databinding.FragmentEnveloppeFinanciereBinding

class EnveloppeFinanciereFragment : Fragment() {

    private var _binding: FragmentEnveloppeFinanciereBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoumissionViewModel by activityViewModels()

    companion object {
        fun newInstance(marcheId: String) = EnveloppeFinanciereFragment().apply {
            arguments = Bundle().apply { putString("marche_id", marcheId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnveloppeFinanciereBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUploadFinancier.setOnClickListener {
            (activity as? SoumissionActivity)?.pickFile("financier")
        }

        binding.etMontant.addTextChangedListener {
            viewModel.montantOffre.value = it.toString()
        }

        viewModel.documentFinancier.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.tvDocFinancierNom.text = "✅ ${uri.lastPathSegment ?: "Offre financière"}"
                binding.tvDocFinancierNom.setTextColor(
                    requireContext().getColor(com.almizan.mobile.R.color.status_green)
                )
            } else {
                binding.tvDocFinancierNom.text = "Aucun document sélectionné"
                binding.tvDocFinancierNom.setTextColor(
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