package com.example.skincerinapp.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.skincerinapp.api.ApiConfig
import com.example.skincerinapp.model.SignupRequest
import com.example.skincerinapp.model.SignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {
    private val apiConfig = ApiConfig()
    fun signUpWithEmailAndPassword(username: String, email: String, password: String, callback: ((Boolean, Throwable?) -> Unit)? = null) {
        val request = SignupRequest(username, email, password)
        val call = apiConfig.getApiService().register(request)

        call.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    response.body()?.message
                    callback?.invoke(true, null)
                } else {
                    Log.e("SignUpViewModel", "Registration failed: ${response.code()}")
                    callback?.invoke(false, null)
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Log.e("SignUpViewModel", "Failed to make registration request: ${t.message}")
                callback?.invoke(false, t)
            }
        })
    }


}
