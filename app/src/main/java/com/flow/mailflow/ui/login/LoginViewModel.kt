package com.flow.mailflow.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers

class LoginViewModel: ViewModel() {
    private var mainRepo = MainRepository()

    fun login(
        loginRequest: LoginRequest
    ) =
        liveData {
            emitSource(mainRepo.login(loginRequest).asLiveData(
                Dispatchers.IO
            ))
        }
}