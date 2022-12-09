package com.kjy.apiloginproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.kjy.apiloginproject.databinding.FragmentMyBinding


class MyFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)
        binding.myLoginPageMainArrow.setOnClickListener(this)
        binding.myLoginPageMainText.setOnClickListener(this)

        // 로그인 후 변경될 텍스트 이벤트 설정
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                binding.myLoginPageMainText.text = "로그인을 해주세요"
            } else if (user != null) {
                binding.myLoginPageMainText.text = "${user?.kakaoAccount?.profile?.nickname}"
            }
        }

        return binding.root
    }

    // 뷰에 대한 클릭 리스너
    override fun onClick(v: View?) {
        when (v?.id) {
            // 클릭시 카카오 로그인 페이지로 이동
            R.id.my_loginPage_mainText -> {
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    // 토큰 정보 보기에 실패했을 경우
                    if (error != null) {
                        val intent = Intent(requireContext(), KakaoLoginActivity::class.java)
                        startActivity(intent)
                    }
                    // 토큰 정보 보기에 성공했을 경우
                    else if (tokenInfo != null) {
                        Toast.makeText(requireContext(), "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            // 클릭시 카카오 로그인 페이지로 이동
            R.id.my_loginPage_mainArrow -> {
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    // 토큰 정보 보기에 실패했을 경우
                    if (error != null) {
                        val intent = Intent(requireContext(), KakaoLoginActivity::class.java)
                        startActivity(intent)
                    }
                    // 토큰 정보 보기에 성공했을 경우
                    else if (tokenInfo != null) {
                        Toast.makeText(requireContext(), "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), ProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

}