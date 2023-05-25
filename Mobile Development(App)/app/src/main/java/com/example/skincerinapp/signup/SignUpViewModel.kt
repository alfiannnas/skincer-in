package com.example.skincerinapp.signup

import androidx.lifecycle.ViewModel
import com.example.skincerinapp.R
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUpWithEmailAndPassword(email: String, password: String, callback: (Boolean, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                val error = task.exception
                callback(false, error)
            }
        }
    }
}
