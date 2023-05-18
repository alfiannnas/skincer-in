package com.example.skincerinapp.login


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.skincerinapp.databinding.ActivityLoginBinding
import com.example.skincerinapp.signup.SignUpActivity
import com.example.skincerinapp.ui.main.MainActivity



class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        setupAction()
    }



    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    Intent(applicationContext, MainActivity::class.java).also{
                        startActivity(it)
                    }
                }
            }
        }

        binding.signupButton.setOnClickListener {
            Intent(applicationContext, SignUpActivity::class.java).also{
                startActivity(it)
            }
        }
    }

}