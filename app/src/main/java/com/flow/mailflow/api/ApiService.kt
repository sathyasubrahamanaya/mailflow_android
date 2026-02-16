package com.flow.mailflow.api

import com.flow.mailflow.data_models.request_data.CreateContactRequest
import com.flow.mailflow.data_models.request_data.FeedBackRequests
import com.flow.mailflow.data_models.request_data.LoginRequest
import com.flow.mailflow.data_models.request_data.RegisterRequest
import com.flow.mailflow.data_models.request_data.SaveDraftRequest
import com.flow.mailflow.data_models.response_data.GetDraftsResponse
import com.flow.mailflow.data_models.response_data.GetNotesResponse
import com.flow.mailflow.data_models.response_data.NoteData
import com.flow.mailflow.data_models.response_data.SearchDraftsResponse
import com.flow.mailflow.data_models.response_data.SearchNotesResponse
import com.flow.mailflow.data_models.response_data.base_response.BaseResponse
import com.flow.mailflow.data_models.response_data.sub_response.GenerateEmailResponse
import com.flow.mailflow.data_models.response_data.sub_response.GetContactsResponse
import com.flow.mailflow.data_models.response_data.sub_response.GetQueriesResponse
import com.flow.mailflow.data_models.response_data.sub_response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

    @POST("support/queries/create")
    suspend fun createQueries(
        @Body requestBody: Map<String, String>
    ): Response<BaseResponse<Any>>

    @POST("support/feedback")
    suspend fun feedback(
        @Body requestBody: FeedBackRequests
    ): Response<BaseResponse<Any>>

    @POST("support/queries/get")
    suspend fun getQueries(
    ):Response<BaseResponse<GetQueriesResponse>>

    @POST("drafts/save")
    suspend fun sendToDrafts(@Body saveDraftRequest: SaveDraftRequest): Response<BaseResponse<Any>>

    @GET("drafts/all")
    suspend fun getAllDrafts(
        @Query("limit") limit: Int = 100
    ): Response<BaseResponse<GetDraftsResponse>>

    @Multipart
    @POST("drafts/search")
    suspend fun searchDrafts(
        @Part file: MultipartBody.Part?,
        @Part("query_text") queryText: RequestBody?,
        @Part("limit") limit: RequestBody?
    ): Response<BaseResponse<SearchDraftsResponse>>

    @DELETE("drafts/delete/{draft_id}")
    suspend fun deleteDraft(
        @Path("draft_id") draftId: String
    ): Response<BaseResponse<Any>>


    // --- NOTES ENDPOINTS ---

    @Multipart
    @POST("/notes/save")
    suspend fun saveNote(
        @Part file: MultipartBody.Part? = null,
        @Part("content") content: RequestBody? = null
    ): Response<BaseResponse<NoteData>>

    @Multipart
    @PUT("/notes/update")
    suspend fun updateNote(
        @Part("note_id") noteId: RequestBody,
        @Part file: MultipartBody.Part? = null,
        @Part("content") content: RequestBody? = null
    ): Response<BaseResponse<NoteData>>

    @GET("notes/all")
    suspend fun getAllNotes(
        @Query("limit") limit: Int = 100
    ): Response<BaseResponse<GetNotesResponse>>

    @Multipart
    @POST("notes/search")
    suspend fun searchNotes(
        @Part file: MultipartBody.Part?,
        @Part("query_text") queryText: RequestBody?,
        @Part("limit") limit: RequestBody?
    ): Response<BaseResponse<SearchNotesResponse>>

    @DELETE("notes/delete/{note_id}")
    suspend fun deleteNote(
        @Path("note_id") noteId: String
    ): Response<BaseResponse<Any>>

}