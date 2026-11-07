package com.example.videoeditor.engine

import android.content.Context
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.effect.Effects
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.example.videoeditor.model.EditState
import com.example.videoeditor.model.ExportConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class VideoRenderer(private val context: Context) {

    sealed interface Progress {
        data object Started : Progress
        data class Success(val file: File) : Progress
        data class Error(val exception: ExportException) : Progress
    }

    fun export(state: EditState, config: ExportConfig, outputFile: File): Flow<Progress> = callbackFlow {
        // 1. Potong Video (Clipping)
        val mediaItem = MediaItem.Builder()
            .setUri(state.videoUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(state.trimStartMs)
                    .setEndPositionMs(state.trimEndMs)
                    .build()
            )
            .build()

        // 2. Kumpulkan Efek
        val videoEffects = mutableListOf<Effect>()

        // Rotasi
        if (state.rotationDegrees != 0f) {
            videoEffects.add(
                ScaleAndRotateTransformation.Builder()
                    .setRotationDegrees(state.rotationDegrees)
                    .build()
            )
        }

        // Penyesuaian Warna (Sesuaikan)
        if (state.brightness != 0f || state.contrast != 1f || state.saturation != 1f) {
            videoEffects.add(CustomShaderEffect(state.brightness, state.contrast, state.saturation))
        }
        
        // Skala Resolusi dari Menu Ekspor
        if (config.resolutionScale != 1f) {
            videoEffects.add(
                ScaleAndRotateTransformation.Builder()
                    .setScale(config.resolutionScale, config.resolutionScale)
                    .build()
            )
        }

        // 3. Bungkus Media Item
        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .setEffects(Effects(listOf(), videoEffects))
            .build()

        // 4. Setup Transformer
        val transformer = Transformer.Builder(context)
            .setVideoMimeType(config.videoMimeType)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    trySend(Progress.Success(outputFile))
                    close()
                }

                override fun onError(composition: Composition, exportResult: ExportResult, exception: ExportException) {
                    trySend(Progress.Error(exception))
                    close()
                }
            })
            .build()

        trySend(Progress.Started)
        transformer.start(editedMediaItem, outputFile.absolutePath)

        awaitClose { transformer.cancel() }
    }
}