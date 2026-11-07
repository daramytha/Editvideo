package com.example.videoeditor.model

import androidx.media3.common.MimeTypes

data class ExportConfig(
    val videoMimeType: String = MimeTypes.VIDEO_H264,
    val videoBitrate: Int = 8_000_000,
    val resolutionScale: Float = 1f, // 1f = 1080p, 0.66f = 720p
    val outputFileName: String = "edited_${System.currentTimeMillis()}.mp4"
)