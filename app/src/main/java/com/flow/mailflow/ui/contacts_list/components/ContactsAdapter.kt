package com.flow.mailflow.ui.contacts_list.components

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flow.mailflow.data_models.response_data.sub_response.Contact
import com.flow.mailflow.data_models.response_data.sub_response.GetContactsResponse
import com.flow.mailflow.databinding.ContactItemBinding

class ContactsAdapter(var context: Context, var list: List<Contact>, var onClick: (GetContactsResponse) -> Unit):
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>(){
    class ViewHolder(var binding: ContactItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ContactItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = list[position]
        holder.binding.apply {
            nameTextView.text = item.name
            emailTextView.text = item.email
            phoneTextView.text = item.phone
        }

    }


}



