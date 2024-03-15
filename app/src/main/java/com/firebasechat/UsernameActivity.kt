package com.firebasechat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebasechat.databinding.ActivityUsernameBinding
import com.google.firebase.Timestamp

class UsernameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsernameBinding
    private var userModel: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarSignup.visibility = View.INVISIBLE
        binding.btnSignup.setOnClickListener {
            signUp()
        }
    }


    private fun signUp() {
        if (binding.edtUsername.text?.trim().toString() == "") {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_LONG).show()
        } else if (binding.edtUsername.text?.trim().toString().length < 3) {
            Toast.makeText(this, "Username must be 3 character require.", Toast.LENGTH_LONG).show()
        } else {
            setUsername()
        }
    }

    private fun setUsername() {
        setInProgress(true)

        userModel = UserModel(
            binding.edtUsername.text?.trim().toString(), intent.extras?.getString("phone").toString(), Timestamp.now()
        )

        FirebaseUtils.currentUserDetails()?.set(userModel!!)?.addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful) {
                val intent = Intent(this@UsernameActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }

    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBarSignup.visibility = View.VISIBLE
        } else {
            binding.progressBarSignup.visibility = View.GONE
        }
    }
}