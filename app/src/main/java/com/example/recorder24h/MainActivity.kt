package com.example.recorder24h

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면을 코드로 직접 만듭니다 (XML 없이 간단하게)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }

        val tvStatus = TextView(this).apply {
            text = "24시간 녹음기 대기중"
            textSize = 20f
            setPadding(0, 0, 0, 50)
        }

        val btnStart = Button(this).apply { text = "녹음 시작" }
        val btnStop = Button(this).apply { text = "녹음 종료" }

        layout.addView(tvStatus)
        layout.addView(btnStart)
        layout.addView(btnStop)
        setContentView(layout)

        checkPermissions()

        btnStart.setOnClickListener {
            val intent = Intent(this, RecordingService::class.java)
            intent.action = "START"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            tvStatus.text = "🔴 녹음 중... (홈 버튼을 눌러 나가세요)"
        }

        btnStop.setOnClickListener {
            val intent = Intent(this, RecordingService::class.java)
            intent.action = "STOP"
            startService(intent)
            tvStatus.text = "⏹ 녹음 중지됨"
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 100)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "모든 파일 접근 권한을 허용해주세요", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }
}
