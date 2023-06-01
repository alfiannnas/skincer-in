package com.example.skincerinapp.api

import com.example.skincerinapp.model.LoginRequest
import com.example.skincerinapp.model.LoginResponse
import com.example.skincerinapp.model.*
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>


    @POST("register")
    fun register(
        @Body request: SignupRequest
    ): Call<SignupResponse>



}