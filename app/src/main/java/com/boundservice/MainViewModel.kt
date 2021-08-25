package com.boundservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

  private val myBinder: MutableLiveData<MyService.MyBinder> = MutableLiveData<MyService.MyBinder>()
  private val isProgressUpdating: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

  private val serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      val binder = service as MyService.MyBinder
      myBinder.postValue(binder)
    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }

  }

  public fun getServiceConnection(): ServiceConnection {
    return serviceConnection
  }

  public fun setProgressUpdating(isUpdating: Boolean) {
    isProgressUpdating.value = isUpdating
  }

  public fun getBinder() : LiveData<MyService.MyBinder> = myBinder

  public fun getProgressUpdating(): MutableLiveData<Boolean> = isProgressUpdating

}