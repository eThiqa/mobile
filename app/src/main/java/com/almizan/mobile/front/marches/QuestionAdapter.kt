package com.almizan.mobile.front.marches


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.data.models.Question
import com.almizan.mobile.databinding.ItemQuestionBinding

class QuestionAdapter : ListAdapter<Question, QuestionAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(q: Question) {
            binding.tvQuestion.text = "❓ ${q.contenu}"
            binding.tvDateQuestion.text = q.dateQuestion
            if (q.reponse != null) {
                binding.tvReponse.text = "💬 ${q.reponse}"
                binding.tvReponse.visibility = android.view.View.VISIBLE
                binding.tvDateReponse.text = q.dateReponse ?: ""
                binding.tvDateReponse.visibility = android.view.View.VISIBLE
            } else {
                binding.tvReponse.visibility = android.view.View.GONE
                binding.tvDateReponse.visibility = android.view.View.GONE
                binding.tvEnAttente.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(a: Question, b: Question) = a.id == b.id
            override fun areContentsTheSame(a: Question, b: Question) = a == b
        }
    }
}