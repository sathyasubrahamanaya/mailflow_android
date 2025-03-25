package com.flow.mailflow.data_models.response_data.sub_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GenerateEmailResponse {
    @SerializedName("email_content")
    @Expose
    var emailContent: EmailContent? = null
}

class EmailContent {
    @SerializedName("recipient_email")
    @Expose
    var recipientEmail: String? = null

    @SerializedName("subject")
    @Expose
    var subject: String? = null

    @SerializedName("body")
    @Expose
    var body: String? = null

    @SerializedName("explanation")
    @Expose
    var explanation: String? = null
}
