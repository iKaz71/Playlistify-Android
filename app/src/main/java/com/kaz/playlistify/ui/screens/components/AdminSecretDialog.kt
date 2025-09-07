package com.kaz.playlistify.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kaz.playlistify.api.RetrofitInstance
import com.kaz.playlistify.model.AdminGrantRequest
import kotlinx.coroutines.launch

@Composable
fun AdminSecretDialog(
    sessionId: String,
    uid: String,
    onSuccess: (message: String) -> Unit,
    onError: (message: String) -> Unit,
    onDismiss: () -> Unit
) {
    var secret by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSecret by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "Convertirme en admin") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Introduce la palabra secreta que aparece en la pantalla del TV.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Palabra secreta") },
                    singleLine = true,
                    visualTransformation = if (showSecret) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showSecret = !showSecret }) {
                            Icon(
                                imageVector = if (showSecret) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showSecret) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading && secret.isNotBlank(),
                onClick = {
                    scope.launch {
                        try {
                            isLoading = true
                            val body = AdminGrantRequest(
                                sessionId = sessionId,
                                uid = uid,
                                secretAttempt = secret.trim()
                            )
                            val resp = RetrofitInstance.sessionApi.grantAdmin(body)
                            val result = resp.body()
                            if (resp.isSuccessful && result != null) {
                                if (result.ok) {
                                    onSuccess(result.message.ifBlank { "Listo, ahora eres admin" })
                                    onDismiss()
                                } else {
                                    onError(result.message.ifBlank { "No se pudo otorgar admin" })
                                }
                            } else {
                                onError("Error ${resp.code()}: ${resp.message()}")
                            }
                        } catch (e: Exception) {
                            onError(e.message ?: "Error de red")
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isLoading) onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}
