package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.auth.login.LoginActivity
import com.dicoding.storyapp.ui.auth.login.LoginViewModel
import com.dicoding.storyapp.ui.map.MapsActivity
import com.dicoding.storyapp.ui.story.AddStoryActivity
import com.dicoding.storyapp.ui.story.StoryAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.dashboard_story)

        val factory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val storyAdapter = StoryAdapter()

        showLoading(true)

        loginViewModel.getUser().observe(this) { user ->
            if (user.userId.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.rvListStory.adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storyAdapter.retry()
                    }
                )
                mainViewModel.getAllStory(user.token).observe(this) {
                    Log.e("List", it.toString())
                    storyAdapter.submitData(lifecycle, it)
                    showLoading(false)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvListStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvListStory.addItemDecoration(itemDecoration)

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_language -> {
                val settingsIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(settingsIntent)
            }

            R.id.action_logout -> {
                loginViewModel.logout()
            }

            R.id.action_go_to_map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
