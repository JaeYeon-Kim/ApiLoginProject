package com.kjy.apiloginproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.user.UserApiClient
import com.kjy.apiloginproject.databinding.ActivityMainBinding
import com.kjy.apiloginproject.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackProfile()

        updateInfo()

        binding.profileLogoutBtn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }

             }
        }

        binding.profileMemberQuitBtn.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        }
    }

    // 핸드폰 뒤로가기 버튼
    override fun onBackPressed() {
        super.onBackPressed()
    }

    // 툴바의 뒤로가기 버튼 클릭시 이전 화면으로
    private fun onBackProfile() {
        binding.backProfileActivity.setOnClickListener {
            onBackPressed()
        }
    }

    // 카카오 로그인 후에 정보 가져오기
    private fun updateInfo() {
        UserApiClient.instance.me { user, error ->
            binding.profileNameText.text = "${user?.kakaoAccount?.profile?.nickname}"
            binding.profileBirthText.text = "${user?.kakaoAccount?.birthday}"
            binding.profileEmailText.text = "${user?.kakaoAccount?.email}"
        }
    }


}