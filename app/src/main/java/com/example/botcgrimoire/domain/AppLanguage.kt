package com.example.botcgrimoire.domain

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class AppLanguage(val languageTag: String) {
    Russian("ru"),
    English("en");

    companion object {
        fun fromLanguageTag(languageTag: String?): AppLanguage {
            return values().firstOrNull { it.languageTag == languageTag } ?: Russian
        }
    }
}

class LanguagePreferences(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getLanguage(): AppLanguage {
        return AppLanguage.fromLanguageTag(preferences.getString(LANGUAGE_KEY, null))
    }

    fun setLanguage(language: AppLanguage) {
        preferences.edit().putString(LANGUAGE_KEY, language.languageTag).apply()
    }

    companion object {
        fun localizedContext(context: Context): Context {
            val language = LanguagePreferences(context).getLanguage()
            val locale = Locale.forLanguageTag(language.languageTag)
            Locale.setDefault(locale)
            val configuration = Configuration(context.resources.configuration).apply {
                setLocale(locale)
                setLayoutDirection(locale)
            }
            return context.createConfigurationContext(configuration)
        }

        private const val PREFERENCES_NAME = "APP_SETTINGS"
        private const val LANGUAGE_KEY = "LANGUAGE"
    }
}
