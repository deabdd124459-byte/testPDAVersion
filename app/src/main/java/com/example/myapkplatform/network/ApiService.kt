package com.example.myapkplatform.network

import com.example.myapkplatform.model.LoginRequest
import com.example.myapkplatform.model.LoginResponse
import com.example.myapkplatform.model.VersionInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET
    fun checkVersion(@Url url: String): Call<VersionInfo>
}
