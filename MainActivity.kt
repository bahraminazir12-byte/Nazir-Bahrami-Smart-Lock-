package com.nazirbhahrami.smartlock

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity

class MainActivity : AppCompatActivity() {
    private val gold = Color.rgb(201, 163, 74)
    private val expectedQr = "NAZIR-BAHRAMI-SMART-LOCK|NB-SMARTLOCK-010E71190B5251551FB4F2A7"
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(Color.rgb(16, 17, 20))
        }
        val title = TextView(this).apply {
            text = "NAZIR BAHRAMI"
            textSize = 27f
            setTextColor(gold)
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "SMART LOCK"
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "برای تأیید هویت، QR چاپ‌شده را اسکن کنید."
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 34, 0, 34)
        }
        val scan = Button(this).apply {
            text = "اسکن QR CODE"
            setTextColor(Color.BLACK)
            setBackgroundColor(gold)
            textSize = 16f
            setOnClickListener { startQrScan() }
        }
        val note = TextView(this).apply {
            text = "این برنامه فقط قفل داخل برنامه را تأیید می‌کند؛ قفل اصلی اندروید را باز یا دور نمی‌زند."
            textSize = 12f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        }
        root.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        root.addView(subtitle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        root.addView(status, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        root.addView(scan, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 56))
        root.addView(note, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        setContentView(root)
    }

    private fun startQrScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 77)
            return
        }
        IntentIntegrator(this)
            .setCaptureActivity(CaptureActivity::class.java)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setPrompt("QR اختصاصی خود را مقابل دوربین بگیرید")
            .setBeepEnabled(false)
            .setOrientationLocked(true)
            .initiateScan()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 77 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startQrScan()
        } else if (requestCode == 77) {
            status.text = "اجازه دوربین داده نشد. برای اسکن QR باید اجازه دوربین را فعال کنید."
        }
    }

    @Deprecated("Deprecated in Android API, retained for scanner compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            status.text = when {
                result.contents == null -> "اسکن لغو شد."
                result.contents == expectedQr -> "تأیید شد! خوش آمدید، Nazir Bahrami."
                else -> "QR معتبر نیست. فقط کد ثبت‌شده پذیرفته می‌شود."
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
