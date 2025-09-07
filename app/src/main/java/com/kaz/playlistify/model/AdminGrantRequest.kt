package com.kaz.playlistify.model

data class AdminGrantRequest(
    val sessionId: String,
    val uid: String,
    val secretAttempt: String
)
