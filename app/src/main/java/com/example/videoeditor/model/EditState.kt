package com.example.videoeditor.model

import android.net.Uri

data class EditState(
    val videoUri: Uri? = null,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    
    // Efek Dasar
    val rotationDegrees: Float = 0f,
    val filterName: String? = null,
    
    // Penyesuaian Warna (Sesuaikan)
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val sharpness: Float = 0f,
    val vignette: Float = 0f
) {
    val durationMs: Long get() = trimEndMs - trimStartMs
}
