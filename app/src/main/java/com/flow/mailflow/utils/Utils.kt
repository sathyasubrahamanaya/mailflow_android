package com.flow.mailflow.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import timber.log.Timber

object Utils {
    fun Any.timberCall(context: Context,name:String,exception: String,toast :Boolean = false){
        if (toast){
            Toast.makeText(context, "$name, : $exception", Toast.LENGTH_SHORT)
                .show()
        }
        Timber.tag(name).e(exception)
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}