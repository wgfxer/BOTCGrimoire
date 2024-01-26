package com.example.botcgrimoire.domain

import android.content.Context

/**
 * @author Valeriy Minnulin
 */
class ResourceManager(
    private val context: Context
) {

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, args)
    }
}