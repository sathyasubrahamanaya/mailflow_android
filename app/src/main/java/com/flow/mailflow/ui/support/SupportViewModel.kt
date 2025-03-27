package com.flow.mailflow.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import retrofit2.http.Query

class SupportViewModel:ViewModel() {
    private var mainRepo = MainRepository()


    fun createQueries(
        query: String
    ) =
        liveData {
            emitSource(mainRepo.createQueries(query).asLiveData(
                Dispatchers.IO
            ))
        }


    fun getQueries() = liveData {
        emitSource(mainRepo.getQueries().asLiveData(Dispatchers.IO))
    }

}