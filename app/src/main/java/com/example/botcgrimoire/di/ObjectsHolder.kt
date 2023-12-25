package com.example.botcgrimoire.di

import android.app.Application
import com.example.botcgrimoire.domain.AppStateInteractor

/**
 * @author Valeriy Minnulin
 */
private var app: Application? = null
val stateInteractor: AppStateInteractor by lazy { AppStateInteractor(app!!.applicationContext) }

@Synchronized
fun getAppStateInteractor(): AppStateInteractor {
    return stateInteractor
}

fun setApplication(application: Application) {
    app = application
}