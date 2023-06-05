package com.example.skincerinapp.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skincerinapp.api.ApiConfig
import com.example.skincerinapp.model.LoginRequest
import com.example.skincerinapp.model.LoginResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    private val apiConfig = ApiConfig()
    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    fun signInWithEmailAndPassword(email: String, password: String) {
        _showProgressBar.value = true
        val request = LoginRequest(email, password)
        val call = apiConfig.getApiService().login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.message == "Welcome Back!") {

                        _loginResult.value = true

                    } else {
                        _loginResult.value = false
                        Log.e("LoginActivity", "Login failed: ${loginResponse?.message}")
                    }
                } else {
                    _loginResult.value = false
                    Log.e("LoginActivity", "Login failed: ${response.code()}")
                }
                _showProgressBar.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = false
                Log.e("LoginActivity", "Failed to make login request: ${t.message}")
                _showProgressBar.value = false
            }
        })

    }

    fun signInWithGoogle(idToken: String) {
        _showProgressBar.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _loginResult.value = task.isSuccessful
                _showProgressBar.value = false
            }
    }

}