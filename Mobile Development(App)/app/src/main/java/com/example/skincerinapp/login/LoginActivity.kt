package com.example.skincerinapp.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivityLoginBinding
import com.example.skincerinapp.signup.SignUpActivity
import com.example.skincerinapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(checkForm()){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this,getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this,getString(R.string.wrong_login), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkForm(): Boolean {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        var isValid = true
        when {
            email.isEmpty() -> {
                binding.emailEditTextLayout.error = getString(R.string.error_enter_email)
                isValid = false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailEditTextLayout.error = getString(R.string.invalid_email)
                isValid = false
            }
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = getString(R.string.error_passrword)
                binding.passwordEditTextLayout.errorIconDrawable = null
                isValid = false
            }
            password.length <= 7 -> {
                binding.passwordEditTextLayout.error = getString(R.string.password_leght)
                binding.passwordEditTextLayout.errorIconDrawable = null
                isValid = false
            }
            else -> {
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
            }
        }
        return isValid
    }

}