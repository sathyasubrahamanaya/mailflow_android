package com.flow.mailflow.data_models.request_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class CreateContactRequest (
    @SerializedName("name")
    @Expose
    private val name: String? = null,

    @SerializedName("email")
    @Expose
    private val email: String? = null,

    @SerializedName("phone")
    @Expose
    private val phone: String? = null
    )