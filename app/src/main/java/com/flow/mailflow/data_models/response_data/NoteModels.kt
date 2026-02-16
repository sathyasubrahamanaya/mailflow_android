package com.flow.mailflow.data_models.response_data



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoteItem (
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("user_id")
    @Expose
    val userId: Int? = null,

    @SerializedName("content")
    @Expose
    val content: String? = null,

    @SerializedName("type")
    @Expose
    val type: String? = null,

    @SerializedName("score")
    @Expose
    val score: Double? = null
)



data class GetNotesResponse (
    @SerializedName("results")
    @Expose
    val results: List<NoteItem>? = null
)

data class SearchNotesResponse (
    @SerializedName("query_used")
    @Expose
    val queryUsed: String? = null,

    @SerializedName("results")
    @Expose
    val results: List<NoteItem>? = null
)

data class NoteData(
    @SerializedName("note_id")
    @Expose
    val noteId: String? = null,

    @SerializedName("content")
    @Expose
    val content: String? = null


)