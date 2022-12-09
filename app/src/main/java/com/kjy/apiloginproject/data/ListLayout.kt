package com.kjy.apiloginproject.data

// 검색 결과를 담을 리사이클러뷰의 클래스
data class ListLayout (val name: String,        //  장소명
                       val phoneNumber: String,     // 전화번호
                       val address: String,         // 주소
                        val x: Double,              // 경도(Longitude)
                        val y: Double)              // 위도(Latitude)