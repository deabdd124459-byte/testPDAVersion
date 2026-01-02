package com.example.myapkplatform.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 登入 API 的請求內文
 */
data class LoginRequest(
    @SerializedName("username")
    val account: String,

    @SerializedName("password")
    val password: String
)

/**
 * 登入 API 的完整回應結構
 */
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: LoginData?
)

/**
 * 登入 API 成功後回傳的「使用者資料」 (位於 data 物件中)
 */
data class LoginData(
    @SerializedName("userId")
    var userId: Int,

    @SerializedName("userNo")
    val userNo: String?,

    @SerializedName("privileges")
    val privileges: List<Privilege>?

) : Serializable

/**
 * 單獨的權限項目
 */
data class Privilege(
    @SerializedName("program")
    val program: String,

    @SerializedName("function")
    val function: String
) : Serializable

/**
 * version.json 的資料模型
 */
data class VersionInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotes: String
)
