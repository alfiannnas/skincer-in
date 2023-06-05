package com.example.skincerinapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("fullName")
	val fullName: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
