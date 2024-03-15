package com.firebasechat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseUtils {
    companion object {
        private fun currentUserId(): String? {
            return FirebaseAuth.getInstance().uid
        }

        fun currentUserDetails(): DocumentReference? {
            return currentUserId()?.let { FirebaseFirestore.getInstance().collection("users").document(it) }
        }

        fun isLoggedIn(): Boolean {
            return currentUserId() != null
        }

    }
}