package com.example.skincerinapp.login

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skincerinapp.api.ApiConfig
import com.example.skincerinapp.model.LoginRequest
import com.example.skincerinapp.model.LoginResponse
import com.example.skincerinapp.ui.main.MainActivity
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
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token


    fun signInWithEmailAndPassword(email: String, password: String) {
        val request = LoginRequest(email, password)
        val call = apiConfig.getApiService().login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {

                    _loginResult.value = true
                    _token.value = response.body()?.token!!

                } else {
                    _loginResult.value = false
                    Log.e("LoginActivity", "Login failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = false
                Log.e("LoginActivity", "Failed to make login request: ${t.message}")
            }
        })
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _loginResult.value = task.isSuccessful
            }
    }



}