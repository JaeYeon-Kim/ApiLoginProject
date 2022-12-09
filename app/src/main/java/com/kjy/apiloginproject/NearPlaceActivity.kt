package com.kjy.apiloginproject


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kjy.apiloginproject.data.KakaoAPI
import com.kjy.apiloginproject.data.ListLayout
import com.kjy.apiloginproject.data.PlaceAdapter
import com.kjy.apiloginproject.data.ResultSearchKeyword
import com.kjy.apiloginproject.databinding.ActivityNearPlaceBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NearPlaceActivity : AppCompatActivity() {

    // request Code
    // gps용
    private val ACCESS_FINE_LOCATION = 1000


    val binding by lazy{
        ActivityNearPlaceBinding.inflate(layoutInflater)
    }

    // 바깥 클래스에서 프로퍼티나 메소드를 접근할수 있게함.
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 3becf7c84966f03a704aa1f3b915af5d"
    }

    // 어댑터와 연결을 위한 데이터 클래스 변수
    private val placeList = arrayListOf<ListLayout>()
    // 검색 페이지번호
    private var pageNumber = 1
    // 검색 키워드
    private var keyWord = ""

    // 어댑터를 가져옴
    private val adapter = PlaceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 어댑터를 액티비티와 연결 해줌
        // 리사이클러뷰의 방향을 기존 가로에서 세로로 변경함, 역방향은 false
        binding.rvList.adapter = adapter
        adapter.placeList = placeList
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        // 어댑터에서 선언한 클릭리스너 인터페이스를 액티비티와 연결해줌
        // 리사이클러뷰이 해당 아이템을 클릭했을때 지도의 중심점이 바뀜
        adapter.setItemClickListener(object: PlaceAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                // 위도와 경도 시스템으로 맵 객체를 생성한다.
                val mapPoint =
                    MapPoint.mapPointWithGeoCoord(placeList[position].y, placeList[position].x)
                // 중심점 변경 + 줌 레벨 변경
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                v.setOnClickListener {
                    val builder = AlertDialog.Builder(this@NearPlaceActivity)
                    val ad = builder.create()
                    builder.setTitle("해당 번호로 전화를 거시겠습니까?")
                        .setMessage("${placeList[position].phoneNumber}")
                        .setPositiveButton("예", DialogInterface.OnClickListener { dialog, i ->
                                val myUri = Uri.parse("tel:${placeList[position].phoneNumber}")
                                val intent = Intent(Intent.ACTION_DIAL, myUri)
                                startActivity(intent)

                        })
                        .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, i ->
                            ad.dismiss()
                        })
                    builder.show()
                }
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                }
        })

        // 검색 버튼
        binding.searchBtn.setOnClickListener {
            keyWord = binding.placeSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyWord, pageNumber)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyWord, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyWord, pageNumber)
        }

        // 백버튼 이미지 클릭시 이전 액티비티로 이동(onBackPressed를 사용함으로서 이전으로 갔을때 이전 프래그먼트 상태 유지)
        binding.placeBackBtn.setOnClickListener {
            onBackPressed()
        }

        // 위치 추적 버튼 클릭시
        binding.myLocationBtn.setOnClickListener {
            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                permissionCheck()
            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }


    // 키워드 검색함수
    private fun searchKeyword(keyword: String, page: Int) {
        //  Retrofit 구성하기
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // 통신 인터페이스를 객체로 생성해주는 역할
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword, page)     // 키, 검색 키워드 페이지를 요청

        // 서버에 요청하기
        call.enqueue(object: Callback<ResultSearchKeyword> {
            // 응답에 성공했을 때
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 응답 결과를 body에 저장
                addItemAndMarkers(response.body())
            }
            // 응답에 실패했을 때
            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("NearPlaceActivity", "통신실패 : ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemAndMarkers(searchResult: ResultSearchKeyword?) {
        // documents = place 데이터 클래스를 리스트로 담는 변수
        // isNullOrEmpty => null이거나 empty 상황이 둘 다 아닐 때 true를 리턴한다.
        // 검색이 들어오면 기존의 리스트 전부 초기화 후 지도의 마커를 전부 제거한다. 그리고 새로 불러온 결과를 리사이클러뷰에 반복문으로 넣어주고
        // 그 결과에 맞는 마커들을 다시 추가해준다.
        // 검색 결과가 있을 경우
        if (!searchResult?.documents.isNullOrEmpty()) {
            placeList.clear()           // 리스트 초기화
            binding.mapView.removeAllPOIItems()     // 지도의 마커 모두 제거
            for(document in searchResult!!.documents) {             // null 이 아님을 강제
                val placeItem = ListLayout(document.place_name,     // 장소명
                                           document.phone,          // 연락처
                                           document.road_address_name, // 도로명 주소
                                           document.x.toDouble(),       // 경도(longitude)
                                           document.y.toDouble())       // 위도(latitude)
                placeList.add(placeItem)            // 리스트에 결과를 넣어줌.

                // 지도에 마커 추가
                val marker = MapPOIItem()
                // apply => 블록내 argument this, 리턴값 블록안의 결과 리턴
                marker.apply {
                    // itemName => MapPOIItem의 이름 조회
                    itemName = document.place_name
                    // mapPoint = 지도 상의 좌표 조회
                    // 위도와 경도를 넣어줌 y = 위도, x = 경도
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin      // 카카오에서 제공하는 블루핀 (클릭 전)
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin   // 카카오에서 제공하는 레드핀 (클릭 후)
                }
                // 설정한 마커를 맵뷰에 적용
                binding.mapView.addPOIItem(marker)
            }
            // 어댑터에 변경 결과를 적용시킴
            adapter.notifyDataSetChanged()
        } else {
            // 검색 결과가 없을 경우
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    // 지도 권한을 위한 권한 체크
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어본다.)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    }
