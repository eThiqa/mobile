package com.almizan.mobile.front.notifications

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.almizan.mobile.data.models.Notification
import com.almizan.mobile.databinding.ItemNotificationBinding

class NotificationAdapter(private val onClick: (Notification) -> Unit) :
    ListAdapter<Notification, NotificationAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: Notification) {
            binding.tvTitle.text = notif.resolveTitle()
            binding.tvMessage.text = notif.resolveMessage()
            binding.tvDate.text = notif.created_at?.take(10) ?: ""

            // Mettre en gras si non lu
            if (!notif.isRead()) {
                binding.tvTitle.setTypeface(null, Typeface.BOLD)
                binding.tvMessage.setTextColor(ContextCompat.getColor(binding.root.context, com.almizan.mobile.R.color.sovereign_navy))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, com.almizan.mobile.R.color.semantic_info_container))
            } else {
                binding.tvTitle.setTypeface(null, Typeface.NORMAL)
                binding.tvMessage.setTextColor(ContextCompat.getColor(binding.root.context, com.almizan.mobile.R.color.text_secondary))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, com.almizan.mobile.R.color.white))
            }

            binding.root.setOnClickListener { onClick(notif) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(a: Notification, b: Notification) = a.id == b.id
            override fun areContentsTheSame(a: Notification, b: Notification) = a == b
        }
    }
}