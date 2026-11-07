package com.example.videoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView
import com.google.android.material.button.MaterialButton

class EditorActivity : AppCompatActivity() {

    private val vm: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val uriString = intent.getStringExtra("VIDEO_URI")
        if (uriString == null) {
            finish()
            return
        }

        // Pura-pura durasi 30 detik
        vm.loadVideo(Uri.parse(uriString), 30_000L)

        // Setup Video Player
        val playerView = findViewById<PlayerView>(R.id.playerView)
        playerView.player = vm.player

        // Tombol Rotasi
        findViewById<MaterialButton>(R.id.btnRotate).setOnClickListener {
            vm.rotate()
            Toast.makeText(this, "Video dirotasi", Toast.LENGTH_SHORT).show()
        }

        // Slider Kecerahan (Contoh Fitur Sesuaikan)
        val sliderBrightness = findViewById<SeekBar>(R.id.sliderBrightness)
        sliderBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = (progress - 50) / 50f // Nilai -1.0 sampai 1.0
                vm.setBrightness(value)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Tombol Lanjut ke Ekspor
        findViewById<MaterialButton>(R.id.btnGoToExport).setOnClickListener {
            startActivity(Intent(this, ExportActivity::class.java))
        }
    }
}