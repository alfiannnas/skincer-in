package com.example.skincerinapp.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.signupButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (checkForm()) {
                viewModel.signUpWithEmailAndPassword(username, email, password) { isSuccess, error ->
                    if (isSuccess) {
                        Toast.makeText(this, getString(R.string.create_succes), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        error?.let {
                            if (it.message == "User with email already exists!") {
                                Toast.makeText(this, getString(R.string.email_already_registered), Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkForm(): Boolean {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()
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
            else -> {
                binding.emailEditTextLayout.error = null
            }
        }

        when {
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = getString(R.string.error_passrword)
                isValid = false
            }
            password.length <= 8 -> {
                binding.passwordEditTextLayout.error = getString(R.string.password_leght)
                isValid = false
            }
            else -> {
                binding.passwordEditTextLayout.error = null
            }
        }

        when {
            confirmPassword.isEmpty() -> {
                binding.confirmPassword.error = getString(R.string.error_passrword)
                isValid = false
            }
            confirmPassword != password -> {
                binding.confirmPassword.error = getString(R.string.error_confirm_password)
                isValid = false
            }
            else -> {
                binding.confirmPassword.error = null
            }
        }

        return isValid
    }
}
