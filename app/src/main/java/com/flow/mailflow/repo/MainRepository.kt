package com.flow.mailflow.repo

import com.flow.mailflow.api.ApiHelper
import com.flow.mailflow.api.ApiHelper.apiService
import com.flow.mailflow.api.ApiState
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.data_models.response_data.base_response.BaseResponse
import com.flow.mailflow.data_models.response_data.sub_response.LoginResponse
import com.flow.mailflow.utils.SharedPreferenceHelper
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


typealias prefs= SharedPreferenceHelper
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

    ): Flow<ApiState<BaseResponse<Any>>> {
        return flow{
            emit(ApiState.loading())
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)

            var bImage: MultipartBody.Part? = null
            if (audio !=null){
                val audioFile = audio
                if (audioFile.exists()) {
                    val reqFile: RequestBody =
                        RequestBody.create("audio/mp3".toMediaTypeOrNull(), audioFile)
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

}