package com.firebasechat

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebasechat.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationBarView


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationBar.selectedItemId = com.firebasechat.R.id.menu_chat


        binding.bottomNavigationBar.setOnItemSelectedListener {
            handleBottomNavigation(it.itemId)
        }

        swapFragments(ChatFragment())
    }


    private fun handleBottomNavigation(
        menuItemId: Int,
    ): Boolean = when (menuItemId) {
        com.firebasechat.R.id.menu_chat -> {
            swapFragments(ChatFragment())
            true
        }

        com.firebasechat.R.id.menu_profile -> {
            swapFragments(ProfileFragment())
            true
        }

        else -> false
    }

    private fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(com.firebasechat.R.id.frameLayout, fragment).commit()
    }
}