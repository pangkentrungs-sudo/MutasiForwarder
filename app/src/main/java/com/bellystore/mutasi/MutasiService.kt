package com.bellystore.mutasi

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MutasiService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val fullMsg = "$title - $text".trim()
        val low = fullMsg.lowercase()

        val keywords = listOf("masuk", "diterima", "berhasil", "qris", "rp", "idr")
        if (keywords.none { low.contains(it) }) return

        val prefs = getSharedPreferences("mutasi_cfg", MODE_PRIVATE)
        val webhookUrl = prefs.getString("webhook_url", "") ?: ""
        if (webhookUrl.startsWith("http")) {
            sendToDiscord(webhookUrl, "💰 **Mutasi Masuk** (`${sbn.packageName}`):\n$fullMsg")
        }
    }

    companion object {
        fun sendToDiscord(webhookUrl: String, content: String) {
            Thread {
                try {
                    val conn = (URL(webhookUrl).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        doOutput = true
                        connectTimeout = 8000
                        readTimeout = 8000
                    }
                    val json = JSONObject().put("content", content).toString()
                    OutputStreamWriter(conn.outputStream).use { it.write(json) }
                    conn.responseCode
                    conn.disconnect()
                } catch (_: Exception) {}
            }.start()
        }
    }
}
