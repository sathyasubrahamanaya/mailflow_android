package com.flow.mailflow.data_models.response_data.base_response

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.json.JSONException

@JsonAdapter(ItemTypeAdapterFactory::class)
class BaseResponse<T> {
    @SerializedName("error_code")
    @Expose
    var errorcode: Int? = null

    @SerializedName("data")
    @Expose
    var data: T? = null
        private set

    @SerializedName("message")
    @Expose
    var message: String? = null

    /////Additional Data Items

    @SerializedName("current_page")
    @Expose
    var currentpage: String? = null

    @SerializedName("total_pages")
    @Expose
    var totalpagr: Int? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("file_id")
    @Expose
    var fileId: Int? = null

    @SerializedName("path")
    @Expose
    var path: String? = null

}

private class ItemTypeAdapterFactory:TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this,type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        return object : TypeAdapter<T>(){
            @Throws(java.io.IOException::class)
            override fun write(out: JsonWriter?, value: T) {
                delegate.write(out,value)
            }
            @Throws(java.io.IOException::class)
            override fun read(inn: JsonReader?): T {
                val jsonElement = elementAdapter.read(inn)
                if (jsonElement.isJsonObject){
                    val jsonObject = jsonElement.asJsonObject
                    //validation errors and other errors can be add here
                    if (jsonObject.get("error_code").asInt == ErrorType.VALIDATION_ERROR.ordinal){
                        if (jsonObject.get("message").isJsonObject){
                            val json = jsonObject.get("message").asJsonObject
                            val iter :Iterator<String> = json.keySet().iterator()
                            while (iter.hasNext()){
                                val key = iter.next()
                                try {
                                    val value:Any = json.get(key)
                                    val error = value as JsonArray
                                    jsonObject.add("message",error[0])
                                    jsonObject.remove("message")

                                }catch (e: JSONException){
                                    // Something went wrong!
                                }
                            }
                        }else if(jsonObject.get("message").isJsonArray){
                            val data = jsonObject.get("message").asJsonArray
                            data?.forEachIndexed{
                                    index, jsonElements ->
                                val item = jsonElements
                                if (item.isJsonArray){
                                    if (item.asJsonArray.size() != 0){
                                        val el = item.asJsonArray.get(0)
                                        if (el.isJsonPrimitive){
                                            jsonObject.add("message", el)
                                            jsonObject.remove("message")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return delegate.fromJsonTree(jsonElement)
            }
        }.nullSafe()
    }

}

enum class ErrorType {
    DUMMY,VALUE_MISSING,AUTHENTICATION_ERROR,VALIDATION_ERROR,BAD_REQUEST
}
