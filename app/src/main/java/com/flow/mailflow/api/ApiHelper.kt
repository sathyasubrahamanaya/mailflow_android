package com.flow.mailflow.api

import com.flow.mailflow.data_models.response_data.base_response.BaseResponse
import com.flow.mailflow.utils.App
import com.flow.mailflow.utils.SharedPreferenceHelper
import com.flow.mailflow.utils.Utils.timberCall
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object ApiHelper {
    val baseUrl = "http://businessrover.in/"

    lateinit var apiService: ApiService

    private val okHttpClient = enableTls12OnPreLollipop(
        OkHttpClient().newBuilder().apply {
            readTimeout(8, TimeUnit.MINUTES)
            connectTimeout(8, TimeUnit.MINUTES)
            writeTimeout(8, TimeUnit.MINUTES)
            interceptors().addAll(getInterceptors())
            ///for DNS
        }).build()

    init {
        makeService()
    }

    private fun makeService() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }


    suspend fun <T> safeApiCall(
        call: suspend () -> Response<T>,
    ): ApiState<T> {
        val errorCode = 3
        val errorMsg = "Something went wrong"

        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                when{
                    response.body() is JsonObject -> {
                        timberCall(App.applicationContext(),"RepoStatus1",response.body().toString())
                        ApiState.success(response.body())
                    }
                    response.body() is String -> {
                        timberCall(App.applicationContext(),"RepoStatus2",response.body().toString())
                        ApiState.success(response.body())
                    }
                    response.body() is BaseResponse<*> -> {
                        val baseResponse = (response.body() as BaseResponse<T>)
                        timberCall(App.applicationContext(),"RepoStatus3",baseResponse.data.toString())
                        val baseErrorCode = baseResponse.errorcode!!
                        timberCall(App.applicationContext(),"baseErrorCode",baseErrorCode.toString())

                        when (baseErrorCode) {
                            0 -> {
                                timberCall(App.applicationContext(),"RepoSuccessBody",response.body().toString())
                                timberCall(App.applicationContext(),"RepoSuccessData",baseResponse.data.toString())
                                ApiState.success(response.body())
                            }
                            1 -> {
                                timberCall(App.applicationContext(),"RepoErrorBody",response.body().toString())
                                timberCall(App.applicationContext(),"RepoErrorData",baseResponse.data.toString())
                                ApiState.error(baseErrorCode, baseResponse.message.toString())
                            }
                            else -> {
                                timberCall(App.applicationContext(),"RepoErrorOtherStatus",response.message().toString())
                                ApiState.error(baseErrorCode, baseResponse.message)
                            }
                        }
                    }
                    else -> {
                        Timber.tag("AnyCase").e(response.body().toString())
                        ApiState.success(response.body())
                    }
                }
            } else {
                Timber.tag("RepoStatus5").e("")
                ApiState.error(response.code(), response.message())
            }
        }catch (e: Exception) {
            Timber.tag("RepoStatus6").e(e)
            ApiState.error(errorCode, errorMsg)
        }
    }








    private fun getInterceptors(): List<Interceptor> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val authInterceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val builder: Request.Builder = original.newBuilder()
                //.header("Authorization", Credentials.basic("api_admin", "admin123"))

                .header("Authorization",
                    "Bearer : ${SharedPreferenceHelper().getString(SharedPreferenceHelper.TOKEN,"")}")
            Timber.tag("BearerToken")
                .e(SharedPreferenceHelper().getString(SharedPreferenceHelper.TOKEN,""))
            Timber.tag("DeviceToken")
                .e(SharedPreferenceHelper().getString(SharedPreferenceHelper.TOKEN,""))

            val request: Request = builder.build()
            chain.proceed(request)
        }
        return mutableListOf<Interceptor>().apply {
            add(loggingInterceptor)
            add(authInterceptor)
        }.toList()
    }

    private fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder {
        try {
            val tlsSocketFactory = TLS12SocketFactory()
            val sc = SSLContext.getInstance("TLSv1.2")
            sc.init(null, null, null)
            tlsSocketFactory.trustManager?.let {
                client.sslSocketFactory(tlsSocketFactory, it)
            }
            val cs: ConnectionSpec =
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2)
                    .build()
            val space: MutableList<ConnectionSpec> = ArrayList()
            space.add(cs)
            space.add(ConnectionSpec.COMPATIBLE_TLS)
            space.add(ConnectionSpec.CLEARTEXT)
            client.connectionSpecs(space)
        } catch (exc: java.lang.Exception) {
            Timber.tag("OkHttpTLSCompat").e(exc, "Error while setting TLS 1.2")
        }
        return client
    }
}