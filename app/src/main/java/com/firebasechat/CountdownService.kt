package com.firebasechat

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class CountdownService : Service() {
    private var countdownTimer: CountDownTimer? = null
    private var remainingTime = 60L

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (remainingTime != 1L) {
                    remainingTime--
                    Log.e("FINISH", remainingTime.toString())
                    // Broadcast remaining time to activity
                    val intent = Intent("COUNTDOWN_TICK")
                    intent.putExtra("remainingTime", remainingTime)
                    sendBroadcast(intent)
                }
                else{
                    val intent = Intent("COUNTDOWN_TICK")
                    intent.putExtra("remainingTime", 0L)
                    sendBroadcast(intent)
                }

            }

            override fun onFinish() {
                // Countdown finished
                Log.e("FINISH", "Finish")
            }
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }
}
