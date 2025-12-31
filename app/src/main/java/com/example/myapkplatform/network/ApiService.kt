package com.example.myapkplatform.network

import com.example.myapkplatform.data.auth.LoginRequest
import com.example.myapkplatform.data.auth.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
