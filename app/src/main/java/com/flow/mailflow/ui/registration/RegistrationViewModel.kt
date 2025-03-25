package com.flow.mailflow.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers

class RegistrationViewModel:ViewModel() {
    private var mainRepo = MainRepository()

    fun register(
        registerRequest: RegisterRequest
    ) =
        liveData {
            emitSource(mainRepo.register(registerRequest).asLiveData(
                Dispatchers.IO
            ))
        }


    fun login(
        loginRequest: LoginRequest
    ) =
        liveData {
            emitSource(mainRepo.login(loginRequest).asLiveData(
                Dispatchers.IO
            ))
        }
}