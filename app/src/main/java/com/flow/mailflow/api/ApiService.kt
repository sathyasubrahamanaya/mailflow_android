package com.flow.mailflow.api

import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.data_models.response_data.base_response.BaseResponse
import com.flow.mailflow.data_models.response_data.sub_response.GenerateEmailResponse
import com.flow.mailflow.data_models.response_data.sub_response.GetContactsResponse
import com.flow.mailflow.data_models.response_data.sub_response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {


    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<BaseResponse<Any>>


    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<BaseResponse<LoginResponse>>


    @Multipart
    @POST("email/generate")
    suspend fun generateMail(
        @Part imageToSend: MultipartBody.Part?,
        @Part("recipient_email") email: RequestBody?,
        @Part("transcribed_text") text: RequestBody?,
        @Part("recipient_name") name: RequestBody?,
    ): Response<BaseResponse<GenerateEmailResponse>>

    @GET("contacts/get")
    suspend fun getContacts(
    ):Response<BaseResponse<GetContactsResponse>>

    @POST("contacts/create")
    suspend fun createContact(
        @Body createContactRequest: CreateContactRequest
    ): Response<BaseResponse<Any>>
}