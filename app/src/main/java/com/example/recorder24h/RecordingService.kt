package com.example.recorder24h

import android.app.*
import android.content.Intent
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordingService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private val CHANNEL_ID = "Recorder24H_Channel"

    // 중요: 내장 메모리 루트에 저장 (/sdcard/Recorder24H)
    private val STORAGE_PATH = Environment.getExternalStorageDirectory().absolutePath + "/Recorder24H/"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START") {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("24H 녹음 중")
                .setContentText("녹음이 진행되고 있습니다.")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .build()
            startForeground(1, notification)
            startRecording()
        } else if (intent?.action == "STOP") {
            stopRecording()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    private fun startRecording() {
        if (isRecording) return
        try {
            val folder = File(STORAGE_PATH)
            if (!folder.exists()) folder.mkdirs()

            val fileName = "${folder.absolutePath}/REC_${System.currentTimeMillis()}.mp3"

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(64000)
                setAudioSamplingRate(22050)
                setOutputFile(fileName)
                prepare()
                start()
            }
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Recording Service", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
