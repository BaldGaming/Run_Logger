package com.example.runlogger

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotionServices {
    @POST("v1/pages")
    suspend fun create_run_page(
        @Body request: NotionPageRequest
    ): Response<Unit>
}