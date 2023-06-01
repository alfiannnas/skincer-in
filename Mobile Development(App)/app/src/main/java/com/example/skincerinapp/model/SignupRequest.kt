package com.example.skincerinapp.model

import com.google.gson.annotations.SerializedName

data class SignupRequest (
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)