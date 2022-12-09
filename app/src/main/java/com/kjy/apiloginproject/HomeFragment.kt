package com.kjy.apiloginproject

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.kjy.apiloginproject.databinding.FragmentHomeBinding
import com.kjy.apiloginproject.pagerAdapter.AdImage
import com.kjy.apiloginproject.pagerAdapter.ViewPagerAdapter


class HomeFragment : Fragment(), View.OnClickListener {

    // 배너의 개수
    private var countBanner = 3

    // 현재 위치를 지정해서 배너가 좌우로 무한 뷰페이저가 가능하게함
    private var currentPosition = Int.MAX_VALUE


    private lateinit var binding: FragmentHomeBinding

    private var adList = mutableListOf<AdImage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.homeCv5.setOnClickListener(this)
        binding.homeMainTitle.setOnClickListener {
            val intent = Intent(requireActivity(), AddressActivity::class.java)
            startActivity(intent)
        }
        binding.homeAdViewPager.setCurrentItem(currentPosition, false)
        viewPager()         // 광고 뷰페이저 함수
        return binding.root
    }

    // 액티비티를 가져와서 클릭리스너 연결
    override fun onClick(v: View?) {
        when(v?.id) {
           // 코로나 버튼 클릭시
           R.id.home_cv_5 -> {
               Toast.makeText(requireActivity(), "코로나 검사 버튼입니다.", Toast.LENGTH_SHORT).show()
               val intent = Intent(requireActivity(), NearPlaceActivity::class.java)
               startActivity(intent)
           }
        }
    }

    // 어댑터와 프래그먼트 연결
    // 어데이터 리스트에
    private fun viewPager() {
        val adapter = ViewPagerAdapter()
        binding.homeAdViewPager.adapter = adapter
        adapter.adList = adList
        adList.apply {
            add(AdImage(R.drawable.viewpager_image_1))
            add(AdImage(R.drawable.viewpager_image_2))
            add(AdImage(R.drawable.viewpager_image_3))
        }

        // 뷰페이저 방향 가로로
        binding.homeAdViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        // 뷰페이저 인디케이터 이벤트
        // 토탈 페이지 텍스트에 배너의 총 개수를 설정해줌
        binding.totalPageText.text = countBanner.toString()

        // 몇번째 인지 변경해주는 이벤트
        binding.homeAdViewPager.apply {
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                // 페이지에 대한 인지 이벤트
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 슬라이드마다 현재 페이지의 인덱스값의 +1 만큼의 값을 텍스트에 적용시킴
                    // position은 0부터 시작
                    binding.currentPageText.text = "${(position % 3) + 1}"
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent()
        intent.getStringExtra("address")
    }
    }

