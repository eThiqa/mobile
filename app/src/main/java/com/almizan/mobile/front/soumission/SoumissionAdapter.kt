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

    inner class ViewHolder(
        val binding: ItemSoumissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(s: Soumission) {

            binding.tvMarcheTitre.text = s.marcheTitre

            binding.tvDateDepot.text =
                "Déposée le ${s.dateDepot.take(10)}"

            val (color, label) = when (s.status) {

                "DRAFT" ->
                    Pair(R.color.status_grey, "Brouillon")

                "SUBMITTED" ->
                    Pair(R.color.accent_blue, "Déposée")

                "WITHDRAWN" ->
                    Pair(R.color.status_red, "Retirée")

                else ->
                    Pair(R.color.status_grey, s.status)
            }

            binding.tvStatut.text = label

            binding.tvStatut.backgroundTintList =
                ContextCompat.getColorStateList(
                    binding.root.context,
                    color
                )

            // Hidden because API doesn't provide them
            binding.tvNote.visibility = View.GONE
            binding.btnVoirNotes.visibility = View.GONE
            binding.tvRang.visibility = View.GONE
            binding.btnRecours.visibility = View.GONE

            binding.btnVoirNotes.setOnClickListener {
                onVoirNotes(s)
            }

            binding.btnRecours.setOnClickListener {
                onDeposerRecours(s)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        ItemSoumissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {

        val DIFF =
            object : DiffUtil.ItemCallback<Soumission>() {

                override fun areItemsTheSame(
                    oldItem: Soumission,
                    newItem: Soumission
                ) = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Soumission,
                    newItem: Soumission
                ) = oldItem == newItem
            }
    }
}