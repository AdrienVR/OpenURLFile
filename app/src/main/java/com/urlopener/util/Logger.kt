package com.urlopener.util

import android.util.Log
import com.urlopener.BuildConfig

object Logger {
    private const val TAG = "OpenURLFile"

    fun d(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, throwable)
        }
    }

    fun i(message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message)
        }
    }

    fun w(message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message)
        }
    }
}
