package com.firebasechat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebasechat.databinding.ActivityOtpverificationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpverificationBinding
    private var phoneNumber = ""

    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var builder: PhoneAuthOptions.Builder
    private var timeoutSeconds = 60L
    private var verificationCode = ""
    var resendingToken: ForceResendingToken? = null

    private fun startCountdownService() {
        intent = Intent(this, CountdownService::class.java)
        startService(intent)
    }

    private val countdownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val remainingTime = intent?.getLongExtra("remainingTime", 0L) ?: 0L
            // Update UI with remaining time
            if (remainingTime == 0L) {
                binding.tvResendOtp.text = "Resend"
                binding.tvResendOtp.isEnabled = true
            } else {
                binding.tvResendOtp.text = remainingTime.toString()
                binding.tvResendOtp.isEnabled = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarVerifyOtp.visibility = View.INVISIBLE
        phoneNumber = intent.extras?.getString("phone").toString()
        Log.e("phoneNumber", phoneNumber)
        sendOTP(phoneNumber, false)
        binding.btnVerifyOTP.setOnClickListener {
            val enteredOtp: String = binding.edtOtp.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp)
            signIn(credential)
        }
        binding.tvResendOtp.setOnClickListener {
            sendOTP(phoneNumber, true)
        }

        binding.tvPhoneNumber.text = phoneNumber
    }

    private fun sendOTP(phoneNumber: String, isResend: Boolean) {
        setInProgress(true)
        startCountdownService()
        registerReceiver(countdownReceiver, IntentFilter("COUNTDOWN_TICK"))
        builder = PhoneAuthOptions.newBuilder(fAuth).setPhoneNumber(phoneNumber).setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(this).setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    setInProgress(false)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OTPVerificationActivity, "OTP sent failed", Toast.LENGTH_LONG).show()
                    Log.e("ERROR", e.message.toString())
                    setInProgress(false)
                    registerReceiver(countdownReceiver, IntentFilter("COUNTDOWN_TICK"))
                    unregisterReceiver(countdownReceiver)
                    stopService(intent)
                    binding.tvResendOtp.text = "Resend"
                    binding.tvResendOtp.isEnabled = true

                }

                override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    verificationCode = s
                    resendingToken = forceResendingToken
                    Toast.makeText(this@OTPVerificationActivity, "OTP sent successfully", Toast.LENGTH_LONG).show()
                    setInProgress(false)
                    Log.e("CODE", verificationCode)
                }
            })

        if (isResend) {
            if (verificationCode != "") {
                resendingToken?.let { builder.setForceResendingToken(it).build() }
                    ?.let { PhoneAuthProvider.verifyPhoneNumber(it) }
            } else {
                PhoneAuthProvider.verifyPhoneNumber(builder.build())
            }
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBarVerifyOtp.visibility = View.VISIBLE
        } else {
            binding.progressBarVerifyOtp.visibility = View.GONE
        }
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential?) {
        //login and go to next activity
        setInProgress(true)
        phoneAuthCredential?.let {
            fAuth.signInWithCredential(it).addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    val intent = Intent(this@OTPVerificationActivity, UsernameActivity::class.java)
                    intent.putExtra("phone", phoneNumber)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@OTPVerificationActivity, "OTP verification failed", Toast.LENGTH_LONG).show()
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        // Register BroadcastReceiver
        registerReceiver(countdownReceiver, IntentFilter("COUNTDOWN_TICK"))
    }

    override fun onPause() {
        super.onPause()
        registerReceiver(countdownReceiver, IntentFilter("COUNTDOWN_TICK"))
        // Unregister BroadcastReceiver
        unregisterReceiver(countdownReceiver)
    }

    override fun onDestroy() {
        registerReceiver(countdownReceiver, IntentFilter("COUNTDOWN_TICK"))
        unregisterReceiver(countdownReceiver)
        stopService(intent)
        super.onDestroy()

    }
}