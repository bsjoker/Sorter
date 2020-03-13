package com.gammagame.vts

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()

        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}