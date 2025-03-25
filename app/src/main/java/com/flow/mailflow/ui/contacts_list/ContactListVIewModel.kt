package com.flow.mailflow.ui.contacts_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers

class ContactListVIewModel: ViewModel() {
    private var mainRepo = MainRepository()

    fun getContacts() = liveData {
        emitSource(mainRepo.getContacts().asLiveData(Dispatchers.IO))
    }
}