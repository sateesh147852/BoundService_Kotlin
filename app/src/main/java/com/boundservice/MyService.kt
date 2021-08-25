package com.boundservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class MyService : Service() {

  lateinit var myBinder: MyBinder
  private var maxValue: Int = 5000
  private var progress: Int = 0
  private lateinit var handler: Handler
  private var isPaused: Boolean = true

  init {
    myBinder = MyBinder()
  }

  override fun onCreate() {
    super.onCreate()
    handler = Handler(Looper.getMainLooper())
  }

  public fun clearTask() {
    isPaused = true
    progress = 0
    updateTask()
  }

  public fun startTask() {
    isPaused = false
    updateTask()

  }

  public fun pauseTask() {
    isPaused = true
    updateTask()
  }

  private fun updateTask() {

    val runnable: Runnable = object : Runnable {
      override fun run() {
        if (progress > maxValue) {
          handler.removeCallbacks(this)
          isPaused = true
        } else if (isPaused) {
          handler.removeCallbacks(this)
        } else {
          progress += 100
          handler.postDelayed(this, 100)
        }
      }

    }
    handler.postDelayed(runnable, 100)

  }

  public fun isPaused(): Boolean = isPaused

  public fun setPaused(isPaused: Boolean) {
    this.isPaused = isPaused
  }

  public fun getMaxValue(): Int = maxValue

  public fun getProgress(): Int = progress

  override fun onBind(intent: Intent?): IBinder? = myBinder

  inner class MyBinder : Binder() {

    fun getService(): MyService {
      return this@MyService
    }

  }
}