package com.example.myapkplatform.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapkplatform.model.ApiResponse
import com.example.myapkplatform.model.LoginRequest
import com.example.myapkplatform.model.LoginResponse
import com.example.myapkplatform.model.VersionInfo
import com.example.myapkplatform.network.RetrofitClient
import com.example.myapkplatform.ui.base.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<ApiResponse<LoginResponse>>()
    val loginResult: LiveData<ApiResponse<LoginResponse>> = _loginResult

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus

    // For update check
    private val _updateResult = MutableLiveData<VersionInfo?>()
    val updateResult: LiveData<VersionInfo?> = _updateResult

    // URL for the version info file - 請將 <您的Gitea網域> 換成您的真實網址
    private val VERSION_URL = "https://raw.githubusercontent.com/deabdd124459-byte/testPDAVersion/refs/heads/beibei_test/docs/version.json"
    fun checkForUpdate() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.checkVersion(VERSION_URL).execute()
                }

                if (response.isSuccessful) {
                    _updateResult.postValue(response.body())
                } else {
                    Log.e("LoginViewModel", "Failed to fetch version info: ${response.errorBody()?.string()}")
                    _updateResult.postValue(null) // Post null on failure
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching version info", e)
                _updateResult.postValue(null) // Post null on error
            }
        }
    }

    fun login(account: String, password: String) {
        viewModelScope.launch {
            _apiStatus.postValue(ApiStatus.LOADING)
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.login(LoginRequest(account, password)).execute()
                }

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    _loginResult.postValue(ApiResponse(responseBody.success, response.code().toString(), responseBody.message, responseBody))
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorMessage = try {
                        // Directly parse the "message" field from the error JSON
                        val errorJson = JSONObject(errorBodyString)
                        errorJson.getString("message")
                    } catch (e: Exception) {
                        // Fallback if parsing fails
                        response.message() ?: "Unknown error"
                    }
                    _loginResult.postValue(ApiResponse(false, response.code().toString(), errorMessage, null))
                }
            } catch (e: Exception) {
                _apiStatus.postValue(ApiStatus.ERROR)
                _loginResult.postValue(ApiResponse(false, "-1", e.message ?: "Network error", null))
            } finally {
                 if (apiStatus.value != ApiStatus.ERROR) {
                    _apiStatus.postValue(ApiStatus.DONE)
                }
            }
        }
    }
}
