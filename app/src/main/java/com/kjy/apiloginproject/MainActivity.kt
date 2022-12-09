package com.kjy.apiloginproject

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.kjy.apiloginproject.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 바텀 네비게이션의 아이콘들의 기본 머터리얼 색상을 없애줌.
        binding.bottomNavMain.itemIconTintList = null

        // 프래그먼트 연결
        initBottomNav()



    }

    private fun initBottomNav() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, HomeFragment())
            .commit()

        binding.bottomNavMain.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, HomeFragment())
                        .commit()
                        return@setOnItemSelectedListener true
                }
                R.id.health -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, HealthFragment())
                        .commit()
                        return@setOnItemSelectedListener true
                }
                R.id.chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ChatFragment())
                        .commit()
                        return@setOnItemSelectedListener true
                }
                R.id.diagnosisList -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ListFragment())
                        .commit()
                        return@setOnItemSelectedListener true
                }
                R.id.my -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, MyFragment())
                        .commit()
                        return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }

            }
        }
    }

}