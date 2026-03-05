package com.example.runlogger

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotionService {
    @POST("v1/pages")
    suspend fun create_run_page(@Body request: NotionPageRequest): Response<Unit>
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.notion.so/"
    private const val AUTH_TOKEN = "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"
    private const val NOTION_VERSION = "2022-06-28"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $AUTH_TOKEN")
            .addHeader("Notion-Version", NOTION_VERSION)
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }.build()

    val api: NotionServices by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotionServices::class.java)
    }
}