package com.flow.mailflow.data_models.request_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeedBackRequests (
    @SerializedName("rating")
    @Expose
    var rating: Int? = null,

    @SerializedName("comment")
    @Expose
    var comment: String? = null
)