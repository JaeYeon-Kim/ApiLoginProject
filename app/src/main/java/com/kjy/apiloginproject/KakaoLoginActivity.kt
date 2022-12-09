package com.kjy.apiloginproject

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kjy.apiloginproject.databinding.ActivityKakaoLoginBinding

class KakaoLoginActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityKakaoLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 카카오 로그인 페이지에서 뒤로가기
        onBackMain()

        // 로그인 페이지 특정 글씨 굵게 하는 커스텀 적용
        loginTextCustom()

        // 카카오 로그인 구현하기
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            // 토큰 정보 보기에 실패했을 경우
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            // 토큰 정보 보기에 성공했을 경우
            else if(tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
            }
        }

        // 카카오 계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인 할 경우 사용
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            // 로그인 할 수 없을때 오류들 나열
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            // 로그인 시도하여 성공하였을시 기존 마이페이지 프래그먼트로 돌아감.
            else if(token != null) {
                onBackPressed()
            }
        }

        // 카카오톡으로 시작하기 버튼 클릭시 카카오톡 로그인 시작
        // 카카오톡으로 로그인을 시도하고 그외의 경우에는 카카오 계정으로 로그인을 시도
        binding.startKakaoTalk.setOnClickListener {
            if(LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)

            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)

            }
        }
    }

    // 특정 글씨만 굵게하는 커스텀
    private fun loginTextCustom() {
        // 텍스트 데이터 변수 획득
        val loginText = binding.loginActivityMainText

        // 변환해주는 Builder 생성
        val builder = SpannableStringBuilder("비대면 진료부터\n약 배달까지\n편하게 누려보세요")

        val boldSpan_1 = StyleSpan(Typeface.BOLD)
        builder.setSpan(boldSpan_1, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val boldSpan_2 = StyleSpan(Typeface.BOLD)
        builder.setSpan(boldSpan_2, 9, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        loginText.text = builder

    }

    // 뒤로가기 버튼 클릭시
    override fun onBackPressed() {
        super.onBackPressed()
    }

    // 뒤로가기 버튼 클릭시 이전 액티비티로 가기
    private fun onBackMain() {
        binding.backLoginActivity.setOnClickListener {
            onBackPressed()
        }
    }
}