package com.flow.mailflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import java.io.File

class HomeViewModel:ViewModel() {
    private var mainRepo = MainRepository()

    fun generateMail(
        email: String?,
        text: String?,
        name: String?,
        audio: File?
    ) =
        liveData {
            emitSource(mainRepo.generateMail(email, text, name, audio).asLiveData(
                Dispatchers.IO
            ))
        }
}