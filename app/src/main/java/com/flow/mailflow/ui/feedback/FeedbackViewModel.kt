package com.flow.mailflow.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.data_models.request_data.FeedBackRequests
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers

class FeedbackViewModel:ViewModel() {
    private var mainRepo = MainRepository()

    fun feedback(
        requestBody: FeedBackRequests
    ) =
        liveData {
            emitSource(mainRepo.feedback(requestBody).asLiveData(
                Dispatchers.IO
            ))
        }
}