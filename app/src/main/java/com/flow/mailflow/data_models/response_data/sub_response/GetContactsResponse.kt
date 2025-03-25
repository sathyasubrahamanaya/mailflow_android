package com.flow.mailflow.data_models.response_data.sub_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetContactsResponse {
    @SerializedName("contacts")
    @Expose
    var contacts: List<Contact>? = null
}

class Contact {
    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null
}
