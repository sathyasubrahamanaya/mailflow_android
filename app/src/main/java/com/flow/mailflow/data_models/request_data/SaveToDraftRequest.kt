package com.flow.mailflow.data_models.request_data

data class SaveDraftRequest(
    val subject: String,
    val body: String,
    val toEmail: String
)
