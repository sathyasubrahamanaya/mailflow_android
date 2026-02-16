package com.flow.mailflow.data_models.response_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DraftItem (
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("user_id")
    @Expose
    val userId: Int? = null,

    @SerializedName("subject")
    @Expose
    val subject: String? = null,

    @SerializedName("body")
    @Expose
    val body: String? = null,

    @SerializedName("to_email")
    @Expose
    val toEmail: String? = null,

    @SerializedName("recipient_name")
    @Expose
    val recipientName: String? = null,

    @SerializedName("status")
    @Expose
    val status: String? = null,

    @SerializedName("score")
    @Expose
    val score: Double? = null
)




data class GetDraftsResponse (
    @SerializedName("results")
    @Expose
    val results: List<DraftItem>? = null
)

data class SearchDraftsResponse (
    @SerializedName("query_used")
    @Expose
    val queryUsed: String? = null,

    @SerializedName("results")
    @Expose
    val results: List<DraftItem>? = null
)