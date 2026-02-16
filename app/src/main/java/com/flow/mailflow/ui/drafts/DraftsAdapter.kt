package com.flow.mailflow.ui.drafts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flow.mailflow.data_models.response_data.DraftItem
import com.flow.mailflow.databinding.ItemDraftBinding

class DraftsAdapter(
    private val onItemClick: (DraftItem) -> Unit,
    private val onDeleteClick: (DraftItem) -> Unit
) : ListAdapter<DraftItem, DraftsAdapter.DraftViewHolder>(DraftDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val binding = ItemDraftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DraftViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DraftViewHolder(private val binding: ItemDraftBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DraftItem) {
            binding.subjectTextView.text = item.subject ?: "(No Subject)"
            binding.emailTextView.text = item.toEmail ?: ""
            binding.bodyPreviewTextView.text = item.body ?: ""
            val scoreVal = item.score ?: 0.0
            if (scoreVal > 0) {
                val percentage = (scoreVal * 100).toInt()
                binding.scoreTextView.text = "$percentage% Match"
                binding.scoreTextView.visibility = View.VISIBLE
            } else {
                binding.scoreTextView.visibility = View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
            binding.deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    class DraftDiffCallback : DiffUtil.ItemCallback<DraftItem>() {
        override fun areItemsTheSame(oldItem: DraftItem, newItem: DraftItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DraftItem, newItem: DraftItem) = oldItem == newItem
    }
}