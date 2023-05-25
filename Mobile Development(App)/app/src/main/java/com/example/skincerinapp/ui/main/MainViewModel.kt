package com.example.skincerinapp.ui.main


import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun logOut() {
        auth.signOut()
    }
}