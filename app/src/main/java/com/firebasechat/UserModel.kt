package com.firebasechat

import com.google.firebase.Timestamp


data class UserModel(var username:String, var phone:String, var timestamp: Timestamp)