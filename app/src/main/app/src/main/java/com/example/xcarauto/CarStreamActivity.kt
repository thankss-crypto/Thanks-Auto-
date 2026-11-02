package com.example.xcarauto

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.apps.auto.sdk.CarActivity
import com.google.android.apps.auto.sdk.CarActivityService

// 1. ตัวเชื่อม Service เข้ากับจอ Android Auto
class CarMainActivityService : CarActivityService() {
    override fun getCarActivity(): Class<out CarActivity> {
        return CarStreamActivity::class.java
    }
}

// 2. หน้าจอเวลาเปิดแอปบนมือถือ
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.loadData(
            "<h2 style='text-align:center;margin-top:40%;font-family:sans-serif;'>XCarAuto พร้อมใช้งาน<br>กรุณาเสียบสายเข้ากับ Android Auto บนรถ</h2>",
            "text/html; charset=utf-8",
            "UTF-8"
        )
    }
}

// 3. หน้าจอหลักบนรถยนต์ (จำลอง Chrome Linux + รวมปุ่มสตรีมมิ่ง)
class CarStreamActivity : CarActivity() {

    private lateinit var webView: WebView
    private val LINUX_CHROME_UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.userAgentString = LINUX_CHROME_UA
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                val resources = request.resources
                for (resource in resources) {
                    if (PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID == resource) {
                        request.grant(resources)
                        return
                    }
                }
                super.onPermissionRequest(request)
            }
        }

        loadDashboard()
    }

    private fun loadDashboard() {
        val dashboardHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { margin: 0; padding: 20px; background-color: #f0f2f5; font-family: sans-serif; user-select: none; }
                    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; color: #333; font-weight: bold; font-size: 18px; }
                    .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; height: 80vh; }
                    .card { border-radius: 16px; display: flex; justify-content: center; align-items: center; color: white; font-size: 26px; font-weight: bold; text-decoration: none; box-shadow: 0 4px 10px rgba(0,0,0,0.12); }
                    .netflix { background: #141414; color: #E50914; }
                    .youtube { background: #ffffff; color: #FF0000; }
                    .disney { background: #04253a; color: #ffffff; }
                    .prime { background: #00A8E1; color: #ffffff; }
                    .hbomax { background: #111111; color: #ffffff; }
                    .viu { background: #F5B400; color: #ffffff; }
                    .wetv { background: #ffffff; color: #FF6000; }
                    .iqiyi { background: #00CC33; color: #ffffff; }
                </style>
            </head>
            <body>
                <div class="header">
                    <span>XCarAuto</span>
                    <span>Streaming Launcher</span>
                </div>
                <div class="grid">
                    <a class="card netflix" href="https://www.netflix.com">NETFLIX</a>
                    <a class="card youtube" href="https://www.youtube.com">YouTube</a>
                    <a class="card disney" href="https://www.disneyplus.com">Disney+</a>
                    <a class="card prime" href="https://www.primevideo.com">prime video</a>
                    <a class="card hbomax" href="https://www.max.com">HBO max</a>
                    <a class="card viu" href="https://www.viu.com">VIU</a>
                    <a class="card wetv" href="https://wetv.vip">WeTV</a>
                    <a class="card iqiyi" href="https://www.iq.com">iQIYI</a>
                </div>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, dashboardHtml, "text/html", "UTF-8", null)
    }
}
