package com.flow.mailflow.ui.drafts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import java.io.File

class DraftsViewModel : ViewModel() {
    private val mainRepo = MainRepository()

    fun getAllDrafts() = liveData {
        emitSource(mainRepo.getAllDrafts().asLiveData(Dispatchers.IO))
    }

    fun searchDrafts(queryText: String?, audioFile: File?) = liveData {
        emitSource(mainRepo.searchDrafts(queryText, audioFile).asLiveData(Dispatchers.IO))
    }

    fun deleteDraft(draftId: String) = liveData {
        emitSource(mainRepo.deleteDraft(draftId).asLiveData(Dispatchers.IO))
    }
}