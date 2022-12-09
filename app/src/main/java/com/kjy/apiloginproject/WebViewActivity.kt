package com.kjy.apiloginproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kjy.apiloginproject.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {


    // 태그
    var TAG = "AddressActivity"

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            // 주소 검색창에서 주소를 선택하면 그 결과값이 data로 들어옴
            // data를 받아서 앱 페이지로 넘김
            Log.d("logFirst", "${data}")
            val intent = Intent(this@WebViewActivity, MainActivity::class.java)
            intent.putExtra("address", data)
        }
    }

    val binding by lazy {
        ActivityWebViewBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 위에서 작성한 블로그 페이지의 url
        // 블로그 의 url을 띄우면 그 후 스크립트에서 작성한 주소 검색의 화면을 띄워준다.
        val blogspot = "https://rlawodusx123.blogspot.com/p/window.html"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(MyJavaScriptInterface(), "Android")
        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url:
            String?) {
                super.onPageFinished(view, url)
                binding.webView.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }
        binding.webView.loadUrl(blogspot)
    }
}