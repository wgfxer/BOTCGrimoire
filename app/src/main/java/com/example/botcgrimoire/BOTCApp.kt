package com.example.botcgrimoire

import android.app.Application
import android.content.Context
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.di.setApplication
import com.example.botcgrimoire.domain.LanguagePreferences

/**
 * @author Valeriy Minnulin
 */
class BOTCApp: Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguagePreferences.localizedContext(base))
    }

    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        getAppStateInteractor().init()
    }
}
