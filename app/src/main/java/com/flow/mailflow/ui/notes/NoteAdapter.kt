package com.flow.mailflow.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flow.mailflow.data_models.response_data.NoteItem // Ensure this import matches your package
import com.flow.mailflow.databinding.ItemNoteBinding

class NotesAdapter(
    private val onItemClick: (NoteItem) -> Unit,
    private val onDeleteClick: (NoteItem) -> Unit
) : ListAdapter<NoteItem, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoteItem) {
            // Set Content
            binding.contentTextView.text = item.content ?: "(No Content)"

            // Handle Match Score (only show if > 0, which happens during search)
            val scoreVal = item.score ?: 0.0
            if (scoreVal > 0) {
                val percentage = (scoreVal * 100).toInt()
                binding.scoreTextView.text = "$percentage% Match"
                binding.scoreTextView.visibility = View.VISIBLE
            } else {
                binding.scoreTextView.visibility = View.GONE
            }

            // Click Listeners
            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteItem>() {
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem == newItem
        }
    }
}