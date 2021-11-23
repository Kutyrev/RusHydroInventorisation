package com.github.kutyrev.rushydroinventorisation

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

class WsLoggerInterceptor{

    val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d("WS_LOGGER", message)
        }
    })


  init {
      logging.level = (HttpLoggingInterceptor.Level.BODY)
  }

}