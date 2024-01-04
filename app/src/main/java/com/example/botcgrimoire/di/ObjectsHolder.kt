package com.example.botcgrimoire.di

import android.app.Application
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.ResourceManager

/**
 * @author Valeriy Minnulin
 */
private var app: Application? = null
val stateInteractor: AppStateInteractor by lazy { AppStateInteractor(app!!.applicationContext) }
val resourceManager: ResourceManager by lazy { ResourceManager(app!!.applicationContext) }

@Synchronized
fun getAppStateInteractor(): AppStateInteractor {
    return stateInteractor
}

fun setApplication(application: Application) {
    app = application
}