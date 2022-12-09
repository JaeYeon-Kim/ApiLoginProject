package com.kjy.apiloginproject

import android.app.Application
import com.kakao.sdk.common.KakaoSdk


// Native App Key로 초기화 과정이 필요함
// 이때 사용하는 클래스
class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "a31336a75b54e73665258f9b87f41c3e")
    }
}