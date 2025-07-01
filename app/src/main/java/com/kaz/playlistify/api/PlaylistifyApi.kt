package com.kaz.playlistify.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


interface PlaylistifyApi {
    // Registrar usuario en sesión
    @POST("session/{sessionId}/user")
    suspend fun registrarUsuario(
        @Path("sessionId") sessionId: String,
        @Body body: RegistrarUsuarioRequest
    ): Response<Unit>

    // Cambiar rol de usuario
    @POST("session/{sessionId}/user/{uid}/role")
    suspend fun cambiarRolUsuario(
        @Path("sessionId") sessionId: String,
        @Path("uid") uid: String,
        @Body body: CambiarRolRequest
    ): Response<CambiarRolResponse>
}

// Data classes:
data class RegistrarUsuarioRequest(
    val uid: String,
    val nombre: String,
    val dispositivo: String,
    val rol: String
)

data class CambiarRolRequest(
    val rol: String,
    val adminUid: String
)

data class CambiarRolResponse(
    val ok: Boolean,
    val message: String
)
