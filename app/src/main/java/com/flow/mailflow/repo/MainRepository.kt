package com.flow.mailflow.repo
import com.flow.mailflow.api.ApiHelper
import com.flow.mailflow.api.ApiHelper.apiService
import com.flow.mailflow.api.ApiState
import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.data_models.request_data.FeedBackRequests
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.data_models.request_data.SaveDraftRequest
import com.flow.mailflow.data_models.response_data.GetDraftsResponse
import com.flow.mailflow.data_models.response_data.SearchDraftsResponse
import com.flow.mailflow.data_models.response_data.base_response.BaseResponse
import com.flow.mailflow.data_models.response_data.sub_response.GenerateEmailResponse
import com.flow.mailflow.data_models.response_data.sub_response.GetContactsResponse
import com.flow.mailflow.data_models.response_data.sub_response.GetQueriesResponse
import com.flow.mailflow.data_models.response_data.sub_response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

class MainRepository {

    suspend fun register(
        registerRequest: RegisterRequest
    ): Flow<ApiState<BaseResponse<Any>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.register(
                   registerRequest
                )
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))

            emit(ApiState.completed(response.errorCode,response.message))
        }.flowOn(Dispatchers.IO)
    }



suspend fun login(
    loginRequest: LoginRequest
    ): Flow<ApiState<BaseResponse<LoginResponse>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.login(
                   loginRequest
                )
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))

            emit(ApiState.completed(response.errorCode,response.message))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun generateMail(
        email: String?,
        text: String?,
        name: String?,
        audio: File?

    ): Flow<ApiState<BaseResponse<GenerateEmailResponse>>> {
        return flow{
            emit(ApiState.loading())
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)

            var bImage: MultipartBody.Part? = null
            if (audio !=null){
                val audioFile = audio
                if (audioFile.exists()) {
                    val reqFile: RequestBody =
                        RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
                    bImage = MultipartBody.Part.createFormData(
                        "file",
                        audioFile.name,
                        reqFile
                    )
                }
            }
            val response = ApiHelper.safeApiCall {
                apiService.generateMail(
                    bImage,
                    email?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    text?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                )
            }

            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))
            emit(ApiState.completed(response.errorCode, response.message))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getContacts():Flow<ApiState<BaseResponse<GetContactsResponse>>>{
        return flow{
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.getContacts()
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))
            emit(ApiState.completed(response.errorCode, response.message))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun createContact(
        createContactRequest: CreateContactRequest
    ): Flow<ApiState<BaseResponse<Any>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.createContact(
                    createContactRequest
                )
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))

            emit(ApiState.completed(response.errorCode,response.message))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createQueries(
        query: String
    ): Flow<ApiState<BaseResponse<Any>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.createQueries(
                    mapOf("query_text" to query)
                )
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))

            emit(ApiState.completed(response.errorCode,response.message))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getQueries():Flow<ApiState<BaseResponse<GetQueriesResponse>>>{
        return flow{
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.getQueries()
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))
            emit(ApiState.completed(response.errorCode, response.message))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun feedback(
        requestBody: FeedBackRequests
    ): Flow<ApiState<BaseResponse<Any>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                apiService.feedback(
                    requestBody
                )
            }
            Timber.tag("RepoStatus").e(response.response.toString())
            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else
                emit(ApiState.error(response.errorCode, response.message))

            emit(ApiState.completed(response.errorCode,response.message))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun sendToDrafts(toEmail: String, subject: String, body: String) = flow {
        emit(ApiState.loading())
        val response = ApiHelper.safeApiCall {
            apiService.sendToDrafts(SaveDraftRequest(subject, body, toEmail))
        }
        if (response.errorCode == 0) {
            emit(ApiState.success(response.response))
        } else {
            emit(ApiState.error(response.errorCode, response.message))
        }
        emit(ApiState.completed(response.errorCode, response.message))
    }.flowOn(Dispatchers.IO)


    suspend fun getAllDrafts(limit: Int = 100): Flow<ApiState<BaseResponse<GetDraftsResponse>>> {
        return flow {
            emit(ApiState.loading())
            val response = ApiHelper.safeApiCall {
                // Using the default limit of 100, or you can pass a custom one
                ApiHelper.apiService.getAllDrafts(limit)
            }
            Timber.tag("RepoStatus").e(response.response.toString())

            if (response.errorCode == 0) {
                emit(ApiState.success(response.response))
            } else {
                emit(ApiState.error(response.errorCode, response.message))
            }

            emit(ApiState.completed(response.errorCode, response.message))
        }.flowOn(Dispatchers.IO)
    }

// In MainRepository.kt

    suspend fun searchDrafts(
        queryText: String?,
        audioFile: File?
    ): Flow<ApiState<BaseResponse<SearchDraftsResponse>>> {
        return flow {
            emit(ApiState.loading())

            // 1. Prepare Audio Part (Matching generateMail pattern)
            var audioPart: MultipartBody.Part? = null
            if (audioFile != null && audioFile.exists()) {
                val reqFile = RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
                audioPart = MultipartBody.Part.createFormData(
                    "file",       // The key name expected by backend
                    audioFile.name,
                    reqFile
                )
            }

            // 2. Prepare Text Parts
            val queryPart = queryText?.toRequestBody("text/plain".toMediaTypeOrNull())
            val limitPart = "100".toRequestBody("text/plain".toMediaTypeOrNull())

            // 3. Make the Call
            val response = ApiHelper.safeApiCall {
                ApiHelper.apiService.searchDrafts(
                    audioPart,
                    queryPart,
                    limitPart
                )
            }

            Timber.tag("RepoStatus").e(response.response.toString())

            if (response.errorCode == 0) {

                emit(ApiState.success(response.response))
            } else {
                emit(ApiState.error(response.errorCode, response.message))
            }

            emit(ApiState.completed(response.errorCode, response.message))

        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteDraft(draftId: String) = flow {
        emit(ApiState.loading())
        val result = ApiHelper.safeApiCall {
            ApiHelper.apiService.deleteDraft(draftId)
        }
        emit(result)
    }

    // --- NOTES REPOSITORY FUNCTIONS ---

    suspend fun saveNote(content: String?, audioFile: File?) = flow {
        emit(ApiState.loading())

        // 1. Prepare Audio Part
        var audioPart: MultipartBody.Part? = null
        if (audioFile != null && audioFile.exists()) {
            val reqFile = RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
            audioPart = MultipartBody.Part.createFormData("file", audioFile.name, reqFile)
        }

        // 2. Prepare Content Part
        val contentPart = content?.toRequestBody("text/plain".toMediaTypeOrNull())

        // 3. Call API
        val result = ApiHelper.safeApiCall {
            ApiHelper.apiService.saveNote(audioPart, contentPart)
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    suspend fun updateNote(noteId: String, content: String?, audioFile: File?) = flow {
        emit(ApiState.loading())

        // 1. Prepare Note ID Part
        val idPart = noteId.toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Prepare Audio Part
        var audioPart: MultipartBody.Part? = null
        if (audioFile != null && audioFile.exists()) {
            val reqFile = RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
            audioPart = MultipartBody.Part.createFormData("file", audioFile.name, reqFile)
        }

        // 3. Prepare Content Part
        val contentPart = content?.toRequestBody("text/plain".toMediaTypeOrNull())

        // 4. Call API
        val result = ApiHelper.safeApiCall {
            ApiHelper.apiService.updateNote(idPart, audioPart, contentPart)
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    suspend fun getAllNotes(limit: Int = 100) = flow {
        emit(ApiState.loading())
        val response = ApiHelper.safeApiCall {
            ApiHelper.apiService.getAllNotes(limit)
        }

        if (response.errorCode == 0) {
            emit(ApiState.success(response.response))
        } else {
            emit(ApiState.error(response.errorCode, response.message))
        }
        emit(ApiState.completed(response.errorCode, response.message))
    }.flowOn(Dispatchers.IO)

    suspend fun searchNotes(queryText: String?, audioFile: File?) = flow {
        emit(ApiState.loading())

        var audioPart: MultipartBody.Part? = null
        if (audioFile != null && audioFile.exists()) {
            val reqFile = RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
            audioPart = MultipartBody.Part.createFormData("file", audioFile.name, reqFile)
        }

        val queryPart = queryText?.toRequestBody("text/plain".toMediaTypeOrNull())
        val limitPart = "100".toRequestBody("text/plain".toMediaTypeOrNull())

        val response = ApiHelper.safeApiCall {
            ApiHelper.apiService.searchNotes(audioPart, queryPart, limitPart)
        }

        if (response.errorCode == 0) {
            // Filter and Sort Logic


            emit(ApiState.success(response.response))
        } else {
            emit(ApiState.error(response.errorCode, response.message))
        }
        emit(ApiState.completed(response.errorCode, response.message))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteNote(noteId: String) = flow {
        emit(ApiState.loading())
        val result = ApiHelper.safeApiCall {
            ApiHelper.apiService.deleteNote(noteId)
        }
        emit(result)
    }.flowOn(Dispatchers.IO)


}