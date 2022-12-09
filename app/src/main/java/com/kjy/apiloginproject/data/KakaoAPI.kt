package com.kjy.apiloginproject.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// API 서버와 통신 시에 사용하는 인터페이스
interface KakaoAPI {
    // json 형식으로 받아옴
    // keyword.json의 정보를 받아온다.
    @GET("/v2/local/search/keyword.json")
    fun getSearchKeyword(
        @Header("Authorization") key: String,       // 카카오 api 키
        @Query("query") query: String, // query => 서버에 전달되는 데이터
        @Query("page") page: Int        // 페이지 번호
    ): Call<ResultSearchKeyword>        // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김.
}