package com.flow.mailflow.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.flow.mailflow.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import java.io.File

class NotesViewModel : ViewModel() {
    private val mainRepo = MainRepository()

    fun saveNote(content: String?, audioFile: File?) = liveData {
        emitSource(mainRepo.saveNote(content, audioFile).asLiveData(Dispatchers.IO))
    }

    fun updateNote(noteId: String, content: String?, audioFile: File?) = liveData {
        emitSource(mainRepo.updateNote(noteId, content, audioFile).asLiveData(Dispatchers.IO))
    }

    fun getAllNotes() = liveData {
        emitSource(mainRepo.getAllNotes().asLiveData(Dispatchers.IO))
    }

    fun searchNotes(queryText: String?, audioFile: File?) = liveData {
        emitSource(mainRepo.searchNotes(queryText, audioFile).asLiveData(Dispatchers.IO))
    }

    fun deleteNote(noteId: String) = liveData {
        emitSource(mainRepo.deleteNote(noteId).asLiveData(Dispatchers.IO))
    }


}