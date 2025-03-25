package com.flow.mailflow.ui.add_contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers

class AddContactsViewModel:ViewModel() {
    private var mainRepo = MainRepository()


    fun createContact(
        createContactRequest: CreateContactRequest
    ) =
        liveData {
            emitSource(mainRepo.createContact(createContactRequest).asLiveData(
                Dispatchers.IO
            ))
        }
}