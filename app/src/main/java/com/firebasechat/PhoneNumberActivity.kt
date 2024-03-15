package com.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebasechat.databinding.ActivityPhoneNumberBinding
import com.firebasechat.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        binding.progressBarSendOtp.visibility = View.INVISIBLE

        binding.btnSendOTP.setOnClickListener {
            sendOtp()
        }

        if (FirebaseUtils.isLoggedIn()) {
            val i = Intent(this@PhoneNumberActivity, HomeActivity::class.java)
            startActivity(i)
        }
        finish()

       /* Handler(Looper.getMainLooper()).postDelayed({

        }, 3000)*/
    }

    private fun sendOtp() {
        if (binding.edtPhoneNumber.text?.trim().toString() == "") {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_LONG).show()
        } else if (binding.edtPhoneNumber.text?.trim().toString().length < 10) {
            Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this@PhoneNumberActivity, OTPVerificationActivity::class.java)
            intent.putExtra("phone", binding.ccp.defaultCountryCodeWithPlus+" "+ binding.edtPhoneNumber.text.toString())
            startActivity(intent)
        }

    }
}