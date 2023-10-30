package com.example.submissionstoryapp.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.databinding.ActivityRegisterBinding
import com.example.submissionstoryapp.view.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            val progressBar = binding.progressBar
            val name = binding.namaEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (isEmailValid(email)) {
                progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val response = ApiConfig.getApiServiceWithToken("").register(name, email, password)
                        progressBar.visibility = View.GONE

                        if (!response.error) {
                            AlertDialog.Builder(this@RegisterActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil daftar. Sudah tidak sabar untuk belajar ya?")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        } else {
                            AlertDialog.Builder(this@RegisterActivity).apply {
                                setTitle("Ooops!")
                                setMessage("${response.message}.")
                                setPositiveButton("Oke") { _, _ ->
                                    val intent = Intent(context, RegisterActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Log.e("Error", e.toString())
                    }
                }

            } else {
                binding.emailEditText.error = "Invalid email address"
            }

        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}