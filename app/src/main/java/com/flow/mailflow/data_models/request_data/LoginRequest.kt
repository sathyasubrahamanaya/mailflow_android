package com.flow.mailflow.data_models.request_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("username")
    @Expose
    var username: String? = null,

    @SerializedName("password")
    @Expose
    var password: String? = null
)