package com.almizan.mobile.front.marches


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.data.models.Question
import com.almizan.mobile.databinding.ItemQuestionBinding

class QuestionAdapter : ListAdapter<Question, QuestionAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(q: Question) {
            binding.tvQuestion.text = "❓ ${q.question_text}"
            binding.tvDateQuestion.text = q.created_at.take(10)
            if (q.answer_text != null) {
                binding.tvReponse.text = "💬 ${q.answer_text}"
                binding.tvReponse.visibility = View.VISIBLE
                binding.tvDateReponse.text = q.answered_at?.take(10) ?: ""
                binding.tvDateReponse.visibility = View.VISIBLE
                binding.tvEnAttente.visibility = View.GONE
            } else {
                binding.tvReponse.visibility = View.GONE
                binding.tvDateReponse.visibility = View.GONE
                binding.tvEnAttente.visibility = View.VISIBLE
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