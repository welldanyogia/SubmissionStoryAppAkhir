package com.example.submissionstoryapp.view.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.pref.UserModel
import com.example.submissionstoryapp.data.response.LoginResponse
import com.example.submissionstoryapp.databinding.ActivityLoginBinding
import com.example.submissionstoryapp.view.ViewModelFactory
import com.example.submissionstoryapp.view.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        setupView()
        setupAction()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction(){
        val progressBar = binding.progressBar
        binding.loginButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val client = ApiConfig.getApiServiceWithToken("").login(email, password)
            client.enqueue(object : Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    progressBar.visibility = View.GONE
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            viewModel.saveSession(
                                UserModel(
                                    responseBody.loginResult.userId,
                                    responseBody.loginResult.name,
                                    responseBody.loginResult.token,
                                    true
                                )
                            )
                            ViewModelFactory.clearInstance()
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }else{
                            progressBar.visibility = View.GONE
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Ooops!")
                                setMessage("Login failed")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    }else{
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Ooops!")
                            setMessage("Login failed")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Oops!")
                        setMessage("${t.message}")
                        setPositiveButton("OK") { _, _ -> }
                        create()
                        show()
                    }
                }

            })
        }
    }
}