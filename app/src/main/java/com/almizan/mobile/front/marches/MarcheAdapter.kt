package com.almizan.mobile.front.marches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.R
import com.almizan.mobile.data.models.Marche
import com.almizan.mobile.data.models.MarcheStatut
import com.almizan.mobile.databinding.ItemMarcheBinding

class MarcheAdapter(private val onClick: (Marche) -> Unit) :
    ListAdapter<Marche, MarcheAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemMarcheBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(marche: Marche) {
            binding.tvTitre.text = marche.titre
            binding.tvReference.text = marche.reference
            binding.tvServiceContractant.text = marche.serviceContractant
            binding.tvWilaya.text = marche.wilaya
            binding.tvTypePill.text = marche.typeMarche
            binding.tvDateLimite.text = "Limite : ${marche.dateLimiteSoumission}"

            // Statut badge
            val (color, label) = when (marche.statut) {
                MarcheStatut.EN_COURS -> Pair(R.color.status_green, "En cours")
                MarcheStatut.CLOTURE -> Pair(R.color.status_grey, "Clôturé")
                MarcheStatut.EVALUATION -> Pair(R.color.status_orange, "Évaluation")
                MarcheStatut.ATTRIBUE_PROVISOIRE -> Pair(R.color.status_blue, "Attribué prov.")
                MarcheStatut.ATTRIBUE_DEFINITIF -> Pair(R.color.status_blue, "Attribué déf.")
                MarcheStatut.ANNULE -> Pair(R.color.status_red, "Annulé")
            }
            binding.tvStatutBadge.text = label
            binding.tvStatutBadge.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, color)

            binding.root.setOnClickListener { onClick(marche) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMarcheBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Marche>() {
            override fun areItemsTheSame(a: Marche, b: Marche) = a.id == b.id
            override fun areContentsTheSame(a: Marche, b: Marche) = a == b
        }
    }
}