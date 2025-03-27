package com.flow.mailflow.data_models.response_data.sub_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GetQueriesResponse {


    @SerializedName("queries")
    @Expose
    var queries: List<QueryListItem>? = null

}

class QueryListItem {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("query_text")
    @Expose
    var queryText: String? = null

    @SerializedName("reply_time")
    @Expose
    var replayTime: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("reply")
    @Expose
    var reply: String? = null
}
