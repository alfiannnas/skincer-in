package com.example.skincerinapp.signup


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.skincerinapp.R
import com.example.skincerinapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar?.hide()

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(checkForm()){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this,getString(R.string.create_succes),Toast.LENGTH_SHORT).show()
                        finish()

                    }
                    else{
                        Toast.makeText(this,getString(R.string.try_again),Toast.LENGTH_SHORT).show()
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
            confirmPassword.isEmpty() -> {
                binding.confirmPasswordEditText.error = getString(R.string.error_passrword)
                isValid = false
            }
            confirmPassword != password -> {
                binding.passwordEditTextLayout.error = getString(R.string.error_confirm_password)
                isValid = false
            }
            else -> {
                // Hapus pesan error jika tidak ada kesalahan
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
                binding.confirmPasswordEditText.error = null
            }
        }
        return isValid
    }



}