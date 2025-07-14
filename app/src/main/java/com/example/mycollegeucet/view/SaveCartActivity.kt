package com.example.mycollegeucet.view

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollegeucet.R


class SaveCartActivity : AppCompatActivity() {
    class MainActivity : AppCompatActivity() {
        private var web: WebView? = null

        override fun onCreate(s: Bundle?) {
            super.onCreate(s)
            setContentView(R.layout.activity_save_cart)

            web = findViewById<WebView?>(R.id.webview)
            val ws = web!!.getSettings()
            ws.setJavaScriptEnabled(true)
            web!!.setWebViewClient(WebViewClient())
            web!!.loadUrl("https://www.geeksforgeeks.org/courses/dsa-self-paced")
        }

        public override fun onBackPressed() {
            if (web!!.canGoBack()) {
                web!!.goBack()
            } else {
                super.onBackPressed()
            }
        }
    }
}
