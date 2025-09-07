package com.kaz.playlistify.api

import com.kaz.playlistify.model.AdminGrantRequest
import com.kaz.playlistify.model.GenericMessage
import com.kaz.playlistify.model.SessionResponse
import com.kaz.playlistify.model.SessionVerifyResponse
import com.kaz.playlistify.model.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {

    @GET("session/{sessionId}")
    suspend fun getSession(@Path("sessionId") sessionId: String): Response<SessionResponse>

    @POST("session/verify")
    suspend fun verifyCode(@Body body: VerifyRequest): Response<SessionVerifyResponse>

    // NUEVO: Convertirse en admin con palabra secreta
    @POST("session/admin/grant")
    suspend fun grantAdmin(@Body body: AdminGrantRequest): Response<GenericMessage>
}
