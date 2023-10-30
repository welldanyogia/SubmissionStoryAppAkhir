package com.example.submissionstoryapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.di.Injection
import com.example.submissionstoryapp.data.response.ListStoryItem
import com.example.submissionstoryapp.data.state.LoadingStateAdapter
import com.example.submissionstoryapp.databinding.ActivityMainBinding
import com.example.submissionstoryapp.view.ViewModelFactory
import com.example.submissionstoryapp.view.addstory.AddStoryActivity
import com.example.submissionstoryapp.view.detail.DetailActivity
import com.example.submissionstoryapp.view.map.MapsActivity
import com.example.submissionstoryapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity(), StoryClickListener {
    private lateinit var binding: ActivityMainBinding
    var token: String = ""
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.clearInstance()
    ViewModelFactory(Injection.provideRepository(this@MainActivity),Injection.provideStoryRepository(this@MainActivity))
    }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this){user ->
            if (!user.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else{
                token = user.token
            }
        }
        setupView()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }
    private fun setupView() {
        storyAdapter = StoryAdapter()
        storyAdapter.setStoryClickListener(this)
        binding.rv.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        addStory()
    }
    private fun fetchStories() {
        viewModel.story.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }
    private fun addStory(){
        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.putExtra("token",token)
            startActivity(intent)
        }
    }

    override fun onStoryClick(story: ListStoryItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("storyId", story.id.toString())
        intent.putExtra("token",token)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout ->{
                viewModel.logout()
                true
            }
            R.id.maps->{
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("token",token)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}