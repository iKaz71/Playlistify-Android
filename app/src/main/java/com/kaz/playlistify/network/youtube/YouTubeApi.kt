package com.kaz.playlistify.network.youtube

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaz.playlistify.BuildConfig
import com.kaz.playlistify.model.YouTubeSearchResponse
import com.kaz.playlistify.model.YouTubeSearchResult
import com.kaz.playlistify.model.YouTubeVideoDetailsResponse
import com.kaz.playlistify.ui.screens.components.VideoItem
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

object YouTubeApi {
    private val client = OkHttpClient()
    private val gson = Gson()

    /** BÚSQUEDA (tu versión original) */
    fun buscarVideos(
        query: String,
        onResult: (List<VideoItem>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (query.isBlank()) {
            onError(Exception("La consulta no puede estar vacía"))
            return
        }

        val apiKey = BuildConfig.YOUTUBE_API_KEY
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=5&q=$encodedQuery&key=$apiKey"

        Log.d("YouTubeApi", "URL: $url")
        val request = Request.Builder().url(url).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val searchResult = gson.fromJson(body, YouTubeSearchResponse::class.java)
                        val videoIds = searchResult.items.map { it.id.videoId }
                        obtenerDetallesDeVideos(searchResult.items, videoIds, onResult, onError)
                    } else {
                        onError(Exception("Error en búsqueda: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                Log.e("YouTubeApi", "❌ Error en búsqueda", e)
                onError(e)
            }
        }.start()
    }

    /** DETALLES EN LOTE (tu versión original) */
    private fun obtenerDetallesDeVideos(
        searchItems: List<YouTubeSearchResult>,
        videoIds: List<String>,
        onResult: (List<VideoItem>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val apiKey = BuildConfig.YOUTUBE_API_KEY
        val ids = videoIds.filterNotNull().joinToString(",")
        val url = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=$ids&key=$apiKey"

        Log.d("YouTubeApi", "Detalles URL: $url")
        val request = Request.Builder().url(url).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val detailsResult = gson.fromJson(body, YouTubeVideoDetailsResponse::class.java)

                        val videos = searchItems
                            .filter { it.id.videoId != null }
                            .mapIndexedNotNull { index, item ->
                                val videoId = item.id.videoId ?: return@mapIndexedNotNull null
                                val duration = detailsResult.items.getOrNull(index)?.contentDetails?.duration ?: "PT0S"
                                try {
                                    VideoItem(
                                        id = videoId,
                                        title = item.snippet.title ?: "Sin título",
                                        thumbnailUrl = item.snippet.thumbnails?.default?.url ?: "",
                                        duration = duration
                                    )
                                } catch (e: Exception) {
                                    Log.e("YouTubeApi", "❌ Error al crear VideoItem", e)
                                    null
                                }
                            }

                        onResult(videos)
                    } else {
                        onError(Exception("Error al obtener detalles: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                Log.e("YouTubeApi", "❌ Error al obtener detalles", e)
                onError(e)
            }
        }.start()
    }

    /** 🔹 DETALLE POR ID (JSON crudo, robusto a tus modelos) */
    fun obtenerDetallesDeVideo(
        videoId: String,
        onResult: (VideoItem) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (videoId.isBlank()) {
            onError(Exception("videoId vacío")); return
        }

        val apiKey = BuildConfig.YOUTUBE_API_KEY
        val url = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&id=$videoId&key=$apiKey"
        Log.d("YouTubeApi", "Detalles por id URL: $url")

        val request = Request.Builder().url(url).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (!response.isSuccessful || body == null) {
                        onError(Exception("Error al obtener detalles por id: ${response.code}"))
                        return@use
                    }

                    val root = gson.fromJson(body, JsonObject::class.java)
                    val items = root.getAsJsonArray("items")
                    if (items == null || items.size() == 0) {
                        onError(Exception("Sin items para ese videoId"))
                        return@use
                    }

                    val first = items[0].asJsonObject
                    val snippet = first.getAsJsonObject("snippet")
                    val title = snippet?.get("title")?.asString ?: "Video de YouTube"
                    val thumbnails = snippet?.getAsJsonObject("thumbnails")
                    val thumbHigh = thumbnails?.getAsJsonObject("high")?.get("url")?.asString
                    val thumbMed  = thumbnails?.getAsJsonObject("medium")?.get("url")?.asString
                    val thumbDef  = thumbnails?.getAsJsonObject("default")?.get("url")?.asString
                    val thumbnailUrl = thumbHigh ?: thumbMed ?: thumbDef ?: "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

                    val contentDetails = first.getAsJsonObject("contentDetails")
                    val durationIso = contentDetails?.get("duration")?.asString ?: "PT0S"

                    onResult(
                        VideoItem(
                            id = videoId,
                            title = title,
                            thumbnailUrl = thumbnailUrl,
                            duration = durationIso
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("YouTubeApi", "❌ Error obtenerDetallesDeVideo", e)
                onError(e)
            }
        }.start()
    }

    /** Utilidad: ISO8601 (PT#H#M#S) → segundos */
    fun parseIso8601DurationToSeconds(iso: String): Int {
        var s = iso.removePrefix("PT").uppercase()
        var h = 0; var m = 0; var sec = 0
        val hIdx = s.indexOf('H')
        if (hIdx != -1) { h = s.substring(0, hIdx).toIntOrNull() ?: 0; s = s.substring(hIdx + 1) }
        val mIdx = s.indexOf('M')
        if (mIdx != -1) { m = s.substring(0, mIdx).toIntOrNull() ?: 0; s = s.substring(mIdx + 1) }
        val sIdx = s.indexOf('S')
        if (sIdx != -1) { sec = s.substring(0, sIdx).toIntOrNull() ?: 0 }
        return h * 3600 + m * 60 + sec
    }
}
