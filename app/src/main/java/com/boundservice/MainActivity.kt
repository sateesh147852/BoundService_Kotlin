package com.boundservice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.boundservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var mainViewModel: MainViewModel
  private lateinit var myService: MyService
  private lateinit var handler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    initialize()
  }

  private fun initialize() {
    handler = Handler(Looper.getMainLooper())
    mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

    mainViewModel.getBinder().observe(this, Observer {
      if (it != null) {
        myService = it.getService()
      }
    })

    binding.btStart.setOnClickListener {
      toggleUpdates()
    }


    mainViewModel.getProgressUpdating().observe(this, Observer {
      val runnable: Runnable = object : Runnable {
        override fun run() {
          if (it) {
            if (myService.getProgress() > myService.getMaxValue()) {
              myService.setPaused(true)
              myService.clearTask()
              handler.removeCallbacks(this)
              binding.btStart.text = "RESTART"
            } else if (myService.isPaused()) {
              myService.setPaused(true)
              handler.removeCallbacks(this)
            } else {
              binding.progressHorizontal.progress = myService.getProgress()
              val data = "" + (myService.getProgress() * 100 / myService.getMaxValue()) + "%"
              binding.tvProgress.text = data
              handler.postDelayed(this, 100)
            }

          }
        }
      }
      handler.postDelayed(runnable, 100)
    })
  }

  private fun toggleUpdates() {
    binding.progressHorizontal.max = myService.getMaxValue()
    if (myService.getProgress() > myService.getMaxValue()) {
      myService.clearTask()
      myService.startTask()
      mainViewModel.setProgressUpdating(true)
      myService.setPaused(false)
      binding.btStart.text = "PAUSE"
    } else if (myService.isPaused()) {
      mainViewModel.setProgressUpdating(true)
      myService.setPaused(false)
      myService.startTask()
      binding.btStart.text = "PAUSE"
    } else {
      mainViewModel.setProgressUpdating(false)
      myService.setPaused(true)
      myService.pauseTask()
      binding.btStart.text = "START"
    }
  }

  override fun onResume() {
    super.onResume()
    val intent = Intent(this, MyService::class.java)
    startService(intent)
    boundService()
  }

  private fun boundService() {
    val intent = Intent(this, MyService::class.java)
    bindService(intent, mainViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE)
  }

  override fun onStop() {
    super.onStop()
    unbindService(mainViewModel.getServiceConnection())
  }

}