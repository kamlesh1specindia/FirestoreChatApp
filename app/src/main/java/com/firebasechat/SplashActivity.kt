package com.firebasechat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebasechat.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var anim: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        installSplashScreen()

        setContentView(binding.root)

        anim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        binding.ivLogo.startAnimation(anim)
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseUtils.isLoggedIn()) {
                val i = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@SplashActivity, PhoneNumberActivity::class.java)
                startActivity(i)
            }
            finish()
        }, 3000)
    }
}