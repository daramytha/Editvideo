package com.example.videoeditor

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PickerActivity : AppCompatActivity() {

    private val pickVideo = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            // Pura-pura durasi video 30 detik (Di produksi, gunakan MediaMetadataRetriever)
            startActivity(Intent(this, EditorActivity::class.java).putExtra("VIDEO_URI", it.toString()))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }
}