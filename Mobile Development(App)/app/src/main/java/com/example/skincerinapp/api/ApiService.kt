package com.example.skincerinapp.api

import com.example.skincerinapp.model.LoginRequest
import com.example.skincerinapp.model.LoginResponse
import com.example.skincerinapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

//    @FormUrlEncoded
//    @POST("v1/register")
//    fun register(
//        @SafeParcelable.Field("name") name: String,
//        @SafeParcelable.Field("email") email: String,
//        @SafeParcelable.Field("password") password: String
//    ): Call<RegisterResponse>
//


}