package com.almizan.mobile.front.soumission

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.R
import com.almizan.mobile.data.models.Soumission
import com.almizan.mobile.data.models.SoumissionStatut
import com.almizan.mobile.databinding.ItemSoumissionBinding

class SoumissionAdapter(
    private val onVoirNotes: (Soumission) -> Unit,
    private val onDeposerRecours: (Soumission) -> Unit
) : ListAdapter<Soumission, SoumissionAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemSoumissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(s: Soumission) {
            binding.tvMarcheTitre.text = s.marcheTitre
            binding.tvDateDepot.text = "Déposée le ${s.dateDepot}"

            val (color, label) = when (s.statut) {
                SoumissionStatut.BROUILLON -> Pair(R.color.status_grey, "Brouillon")
                SoumissionStatut.DEPOSEE -> Pair(R.color.accent_blue, "Déposée")
                SoumissionStatut.EN_EVALUATION -> Pair(R.color.accent_orange, "En évaluation")
                SoumissionStatut.ELIMINEE_ADMIN -> Pair(R.color.status_red, "Éliminée (admin)")
                SoumissionStatut.ELIMINEE_TECHNIQUE -> Pair(R.color.status_red, "Éliminée (technique)")
                SoumissionStatut.NOTEE -> Pair(R.color.status_blue, "Notée")
                SoumissionStatut.ATTRIBUTAIRE -> Pair(R.color.status_green, "🏆 Attributaire")
                SoumissionStatut.PERDANTE -> Pair(R.color.status_grey, "Non retenue")
            }
            binding.tvStatut.text = label
            binding.tvStatut.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, color)

            // Notes
            if (s.noteGlobale != null) {
                binding.tvNote.text = "Note globale : %.2f / 100".format(s.noteGlobale)
                binding.tvNote.visibility = android.view.View.VISIBLE
                binding.btnVoirNotes.visibility = android.view.View.VISIBLE
            }
            if (s.rang != null) {
                binding.tvRang.text = "Classement : #${s.rang}"
                binding.tvRang.visibility = android.view.View.VISIBLE
            }

            // Recours
            binding.btnRecours.visibility =
                if (s.recoursPossible && !s.recoursDepose) android.view.View.VISIBLE
                else android.view.View.GONE

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