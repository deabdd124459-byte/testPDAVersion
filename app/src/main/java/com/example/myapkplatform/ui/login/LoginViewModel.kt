package com.example.myapkplatform.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapkplatform.data.ApiResponse
import com.example.myapkplatform.data.auth.LoginRequest
import com.example.myapkplatform.data.auth.LoginResponse
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
