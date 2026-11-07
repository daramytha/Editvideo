package com.example.videoeditor

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoeditor.engine.VideoRenderer
import com.example.videoeditor.model.EditState
import com.example.videoeditor.model.ExportConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel(app: Application) : AndroidViewModel(app) {

    private val renderer = VideoRenderer(app)

    private val _state = MutableStateFlow(EditState())
    val state: StateFlow<EditState> = _state

    val player: ExoPlayer = ExoPlayer.Builder(app).build().apply {
        repeatMode = ExoPlayer.REPEAT_MODE_ONE
    }

    fun loadVideo(uri: Uri, durationMs: Long) {
        _state.value = EditState(
            videoUri = uri,
            trimStartMs = 0,
            trimEndMs = durationMs
        )
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.playWhenReady = true
    }

    fun rotate() {
        _state.value = _state.value.copy(
            rotationDegrees = (_state.value.rotationDegrees + 90f) % 360f
        )
    }

    fun setBrightness(value: Float) {
        _state.value = _state.value.copy(brightness = value)
    }

    fun setContrast(value: Float) {
        _state.value = _state.value.copy(contrast = value)
    }

    fun exportVideo(config: ExportConfig, onResult: (File?, String?) -> Unit) {
        viewModelScope.launch {
            val outFile = File(getApplication<Application>().getExternalFilesDir(null), config.outputFileName)
            
            renderer.export(_state.value, config, outFile).collect { progress ->
                when (progress) {
                    is VideoRenderer.Progress.Success -> onResult(progress.file, null)
                    is VideoRenderer.Progress.Error -> onResult(null, progress.exception.message)
                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        player.release()
    }
}