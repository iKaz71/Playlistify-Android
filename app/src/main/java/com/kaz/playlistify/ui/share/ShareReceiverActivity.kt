package com.kaz.playlistify.ui.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.kaz.playlistify.api.QueueApi
import com.kaz.playlistify.api.RetrofitInstance
import com.kaz.playlistify.model.CancionRequest
import com.kaz.playlistify.network.youtube.YouTubeApi
import com.kaz.playlistify.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareReceiverActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action
        val type = intent?.type

        when {
            Intent.ACTION_SEND == action && type != null -> handleSend(intent)
            Intent.ACTION_SEND_MULTIPLE == action && type != null -> handleSendMultiple(intent)
            else -> finish()
        }
    }

    private fun handleSend(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        val sharedUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val link = sharedText ?: sharedUri?.toString()

        val videoId = parseVideoIdFromSharedText(link)
        if (videoId == null) {
            Toast.makeText(this, "No se detectó un enlace válido de YouTube", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        // Pide metadatos (1 unidad) para mostrar tarjeta completa en la cola
        YouTubeApi.obtenerDetallesDeVideo(
            videoId = videoId,
            onResult = { v ->
                val titulo = v.title ?: "Video de YouTube"
                val thumb = v.thumbnailUrl ?: "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                val isoDuration = v.duration ?: "PT0S" // tu backend espera String

                lifecycleScope.launch {
                    val ok = enqueueViaQueueApi(
                        queueApi = RetrofitInstance.queueApi,
                        videoId = videoId,
                        titulo = titulo,
                        thumbnailUrl = thumb,
                        durationIso = isoDuration
                    )
                    Toast.makeText(
                        this@ShareReceiverActivity,
                        if (ok) "Agregada a la sala" else "No se pudo agregar",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            },
            onError = {
                // Fallback: encola con metadatos mínimos
                lifecycleScope.launch {
                    val ok = enqueueViaQueueApi(
                        queueApi = RetrofitInstance.queueApi,
                        videoId = videoId,
                        titulo = "Video de YouTube",
                        thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                        durationIso = "PT0S"
                    )
                    Toast.makeText(
                        this@ShareReceiverActivity,
                        if (ok) "Agregada (sin metadatos por ahora)" else "No se pudo agregar",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        )
    }

    private fun handleSendMultiple(intent: Intent) {
        val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
        val first = uris.firstOrNull()?.toString()
        val videoId = parseVideoIdFromSharedText(first)
        if (videoId != null) {
            val singleIntent = Intent().apply { putExtra(Intent.EXTRA_TEXT, first) }
            handleSend(singleIntent)
        } else {
            Toast.makeText(this, "No se detectaron enlaces válidos", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /** Encola usando tu QueueApi y tu CancionRequest */
    private suspend fun enqueueViaQueueApi(
        queueApi: QueueApi,
        videoId: String,
        titulo: String,
        thumbnailUrl: String,
        durationIso: String
    ): Boolean {
        // Lee datos de sesión/usuario del mismo lugar que tu app
        val sessionId = SessionManager.obtenerSessionIdGuardado(this@ShareReceiverActivity) ?: return false
        val uid = SessionManager.obtenerUid(this@ShareReceiverActivity) ?: return false
        val usuario = SessionManager.obtenerNombre(this@ShareReceiverActivity) ?: uid  // fallback

        val body = CancionRequest(
            sessionId = sessionId,
            id = videoId,
            titulo = titulo,
            usuario = usuario,
            thumbnailUrl = thumbnailUrl,
            duration = durationIso,   // ISO PT#M#S, coincide con tu modelo
            uid = uid
        )

        return withContext(Dispatchers.IO) {
            try {
                val resp = queueApi.agregarCancion(body)
                resp.isSuccessful
            } catch (_: Exception) {
                false
            }
        }
    }

    // -------- Parser de URL --------
    private fun parseVideoIdFromSharedText(sharedText: String?): String? {
        if (sharedText.isNullOrBlank()) return null
        val url = extractFirstUrl(sharedText) ?: return null
        return extractVideoIdFromUrl(url)
    }

    private fun extractFirstUrl(text: String): String? {
        val m = Patterns.WEB_URL.matcher(text)
        return if (m.find()) text.substring(m.start(), m.end()) else null
    }

    private fun extractVideoIdFromUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val uri = Uri.parse(url)
        val host = uri.host?.lowercase() ?: return null
        val path = uri.path ?: ""

        if (host.endsWith("youtu.be")) {
            val id = uri.lastPathSegment
            if (!id.isNullOrBlank() && id.matches(Regex("^[A-Za-z0-9_-]{11}$"))) return id
        }
        if (host.contains("youtube.com")) {
            if (path.startsWith("/watch")) {
                val id = uri.getQueryParameter("v")
                if (!id.isNullOrBlank() && id.matches(Regex("^[A-Za-z0-9_-]{11}$"))) return id
            }
            if (path.startsWith("/shorts/")) {
                val id = path.removePrefix("/shorts/").substringBefore('/', "")
                if (id.matches(Regex("^[A-Za-z0-9_-]{11}$"))) return id
            }
        }
        return null
    }
}
