package com.kaz.playlistify.ui.screens.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Helper para pedir permiso de cámara
@Composable
fun RequestCameraPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionState = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA))
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        if (permissionState.value == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}

// Componente QRScanner que pide permiso antes de mostrar la cámara
@Composable
fun QRScanner(
    onResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    var permisoOk by remember { mutableStateOf(false) }
    var denied by remember { mutableStateOf(false) }
    var hasResult by remember { mutableStateOf(false) }

    if (!permisoOk && !denied) {
        RequestCameraPermission(
            onGranted = { permisoOk = true },
            onDenied = { denied = true }
        )
    }

    if (permisoOk) {
        AlertDialog(
            onDismissRequest = {
                if (!hasResult) onCancel()
            },
            title = { Text("Escanea el QR") },
            text = {
                CameraPreview(
                    onQrScanned = {
                        if (!hasResult) {
                            hasResult = true
                            onResult(it)
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { onCancel() }) { Text("Cancelar") }
            },
            dismissButton = {}
        )
    }

    if (denied) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Permiso de cámara requerido") },
            text = { Text("Para escanear códigos QR debes dar acceso a la cámara.") },
            confirmButton = {
                TextButton(onClick = onCancel) { Text("Cerrar") }
            }
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        onDispose { cameraExecutor?.shutdown() }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder()
                    .setTargetResolution(Size(640, 640))
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(640, 640))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val scanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )

                imageAnalysis.setAnalyzer(cameraExecutor!!, { imageProxy ->
                    processImageProxy(scanner, imageProxy, onQrScanned)
                })

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQrScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let {
                        onQrScanned(it)
                    }
                }
            }
            .addOnFailureListener {
                // No-op
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
