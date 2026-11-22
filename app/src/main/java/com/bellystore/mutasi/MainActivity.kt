package com.bellystore.mutasi

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("mutasi_cfg", MODE_PRIVATE)
        val etWebhook = findViewById<EditText>(R.id.etWebhook)
        etWebhook.setText(prefs.getString("webhook_url", ""))

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val url = etWebhook.text.toString().trim()
            prefs.edit().putString("webhook_url", url).apply()
            Toast.makeText(this, "URL Webhook tersimpan!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPerm).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            val url = etWebhook.text.toString().trim()
            if (!url.startsWith("http")) {
                Toast.makeText(this, "Masukkan URL Webhook yang valid dulu!", Toast.LENGTH_SHORT).show()
            } else {
                MutasiService.sendToDiscord(url, "✅ **Tes Koneksi Berhasil!** Pembayaran masuk Rp 10.015 (QRIS)")
                Toast.makeText(this, "Pesan tes dikirim ke Discord!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
