package com.example.videoeditor

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MimeTypes
import com.example.videoeditor.model.ExportConfig
import com.google.android.material.button.MaterialButton
import java.io.File

class ExportActivity : AppCompatActivity() {

    private val vm: EditorViewModel by viewModels()
    private var selectedBitrate = 10_000_000
    private var selectedScale = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        // Slider Resolusi
        val sbResolution = findViewById<SeekBar>(R.id.sbResolution)
        val tvEstSize = findViewById<TextView>(R.id.tvEstimatedSize)
        
        sbResolution.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedScale = when(progress) {
                    0 -> 0.44f // 480p
                    1 -> 0.66f // 720p
                    2 -> 1f    // 1080p
                    3 -> 1.33f // 2k
                    4 -> 2f    // 4k
                    else -> 1f
                }
                updateEstimatedSize(tvEstSize)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Tombol Ekspor
        findViewById<MaterialButton>(R.id.btnExportTop).setOnClickListener {
            val config = ExportConfig(
                videoMimeType = MimeTypes.VIDEO_H264,
                videoBitrate = selectedBitrate,
                resolutionScale = selectedScale
            )

            Toast.makeText(this, "Memproses video...", Toast.LENGTH_SHORT).show()

            vm.exportVideo(config) { file, error ->
                runOnUiThread {
                    if (file != null) {
                        Toast.makeText(this, "Sukses! Tersimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Gagal: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateEstimatedSize(tv: TextView) {
        // Asumsi durasi 30 detik
        val durationSec = 30.0
        val sizeMb = ((selectedBitrate / 1_000_000.0) * durationSec) / 8.0
        tv.text = "Perkiraan ukuran file: ${String.format("%.1f", sizeMb)} MB"
    }
}