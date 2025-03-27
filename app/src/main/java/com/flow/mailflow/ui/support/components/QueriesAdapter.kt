package com.flow.mailflow.ui.support.components

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.flow.mailflow.data_models.response_data.sub_response.QueryListItem
import com.flow.mailflow.databinding.QueryItemBinding
import com.flow.mailflow.ui.support.SupportActivity

class QueriesAdapter(
    var context: Context,
    var queries: List<QueryListItem>,
    var onItemClick: (QueryListItem) -> Unit
) : RecyclerView.Adapter<QueriesAdapter.ViewHolder>() {
    class ViewHolder(var binding: QueryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QueryItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return queries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = queries[position]
        holder.binding.apply {
            titleTextView.text = item.queryText
            if(item.status == "open"){
                statusTextView.text = "Pending"
                replyTextView.isVisible = false
                statusTextView2.isVisible = false
            }else{
                replyTextView.text = item.reply
                statusTextView.text = "Answered"
                statusTextView2.text = "Closed At ${item.replayTime}"
                replyTextView.isVisible = true
                statusTextView2.isVisible = true
            }
            parentLay.setOnClickListener { onItemClick(item) }
        }
    }

}
