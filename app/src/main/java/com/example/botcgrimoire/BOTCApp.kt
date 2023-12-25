package com.example.botcgrimoire

import android.app.Application
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.di.setApplication

/**
 * @author Valeriy Minnulin
 */
class BOTCApp: Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        getAppStateInteractor().init()
    }
}