package com.almizan.mobile.front.soumission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.R
import com.almizan.mobile.data.models.Soumission
import com.almizan.mobile.databinding.ItemSoumissionBinding

class SoumissionAdapter(
    private val onVoirNotes: (Soumission) -> Unit,
    private val onDeposerRecours: (Soumission) -> Unit
) : ListAdapter<Soumission, SoumissionAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemSoumissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(s: Soumission) {
            binding.tvMarcheTitre.text = s.marcheTitre
            binding.tvDateDepot.text = "Déposée le ${s.resolveDate()}"

            val (color, label) = when (s.resolveStatut()) {
                "BROUILLON"          -> Pair(R.color.status_grey,   "Brouillon")
                "DEPOSEE"            -> Pair(R.color.accent_blue,   "Déposée")
                "EN_EVALUATION"      -> Pair(R.color.accent_orange, "En évaluation")
                "ELIMINEE_ADMIN"     -> Pair(R.color.status_red,    "Éliminée (admin)")
                "ELIMINEE_TECHNIQUE" -> Pair(R.color.status_red,    "Éliminée (technique)")
                "NOTEE"              -> Pair(R.color.status_blue,   "Notée")
                "ATTRIBUTAIRE"       -> Pair(R.color.status_green,  "🏆 Attributaire")
                "PERDANTE"           -> Pair(R.color.status_grey,   "Non retenue")
                else                 -> Pair(R.color.status_grey,   s.resolveStatut())
            }

            binding.tvStatut.text = label
            binding.tvStatut.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, color)

            if (s.noteGlobale != null) {
                binding.tvNote.text = "Note globale : %.2f / 100".format(s.noteGlobale)
                binding.tvNote.visibility = View.VISIBLE
                binding.btnVoirNotes.visibility = View.VISIBLE
            } else {
                binding.tvNote.visibility = View.GONE
                binding.btnVoirNotes.visibility = View.GONE
            }

            if (s.rang != null) {
                binding.tvRang.text = "Classement : #${s.rang}"
                binding.tvRang.visibility = View.VISIBLE
            } else {
                binding.tvRang.visibility = View.GONE
            }

            binding.btnRecours.visibility =
                if (s.recoursPossible && !s.recoursDepose) View.VISIBLE else View.GONE

            binding.btnVoirNotes.setOnClickListener { onVoirNotes(s) }
            binding.btnRecours.setOnClickListener { onDeposerRecours(s) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSoumissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Soumission>() {
            override fun areItemsTheSame(a: Soumission, b: Soumission) = a.id == b.id
            override fun areContentsTheSame(a: Soumission, b: Soumission) = a == b
        }
    }
}