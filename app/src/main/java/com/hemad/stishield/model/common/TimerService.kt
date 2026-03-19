package com.hemad.stishield.model.common

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TimerService : Service() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var userDataRepository: UserDataRepository
    private var elapsedTime: Long = 0
    private var initialTotalUsage: Long? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()
        Log.d("TimerService", "Service Created")
        handler = Handler(Looper.getMainLooper())
        userDataRepository = UserDataRepository(applicationContext)
        initialTotalUsage = userDataRepository.usageTime
        startTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "Service Started")
        return START_STICKY
    }

    private fun startTracking() {
        runnable = object : Runnable {
            override fun run() {
                elapsedTime += 1000
                userDataRepository.usageTime += 1000
                if (elapsedTime >= 300000) {
                    userDataRepository.points += 10
                    elapsedTime = 0
                }
                if (userDataRepository.usageTime - initialTotalUsage!! >= 120000) {
                    serviceScope.launch {
                        try {
                            initialTotalUsage = userDataRepository.usageTime
                            userDataRepository.updateUsageTime()
                        } catch (e: Exception) {
                            Log.e("TimerService", "Failed to update usage time: ${e.message}")
                        }
                    }
                }
                handler?.postDelayed(this, 1000)
            }
        }
        handler?.post(runnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks(runnable!!)
        serviceScope.cancel()
        Log.d("TimerService", "Service Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}


