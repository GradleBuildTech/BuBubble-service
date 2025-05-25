package com.example.bubBubbleService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bubService.utils.ext.isServiceRunningInForeground

class BootCompletedIntentReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED ) {
            if(context?.isServiceRunningInForeground(TestBubbleService::class.java) == false) {
                val pushIntent = Intent(context, TestBubbleService::class.java)
                context.startService(pushIntent)
            }
        }
    }
}