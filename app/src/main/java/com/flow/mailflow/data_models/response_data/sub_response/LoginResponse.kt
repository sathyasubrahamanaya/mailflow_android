package com.flow.mailflow.data_models.response_data.sub_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class LoginResponse {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null
}