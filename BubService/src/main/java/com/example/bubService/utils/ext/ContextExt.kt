@file:Suppress("DEPRECATION")

package com.example.bubService.utils.ext

import android.app.ActivityManager
import android.content.Context

fun Context.isServiceRunningInForeground(serviceClass: Class<*>): Boolean {
    val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            if (service.foreground) {
                return true
            }
        }
    }
    return false
}