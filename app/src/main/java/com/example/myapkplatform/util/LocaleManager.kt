package com.example.myapkplatform.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setLocale(context: Context, languageTag: String): Context {
        persistLanguage(context, languageTag)
        return updateResources(context, languageTag)
    }

    fun getLanguage(context: Context): String {
        val defaultLanguage = Locale.getDefault().toLanguageTag()
        return getPreferences(context).getString(KEY_LANGUAGE, defaultLanguage) ?: defaultLanguage
    }

    private fun persistLanguage(context: Context, languageTag: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_LANGUAGE, languageTag)
        editor.apply()
    }

    private fun updateResources(context: Context, languageTag: String): Context {
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)

        val res: Resources = context.resources
        val config: Configuration = Configuration(res.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * 這個方法是整個機制的關鍵！
     * BaseActivity 會在自己被建立之前，先呼叫這個方法。
     * 它會讀取儲存的語言設定，並回傳一個「包裹」了新語言設定的 Context，
     * 確保 Activity 從一開始就使用正確的語言。
     */
    fun attachBaseContext(context: Context): Context {
        // 從 SharedPreferences 讀取儲存的語言標籤 (例如 "en" 或 "zh-TW")
        val languageTag = getPreferences(context).getString(KEY_LANGUAGE, null)
        // 如果有儲存的語言，就用它來更新 Context；如果沒有，就直接回傳原始的 Context
        return if (languageTag != null) {
            updateResources(context, languageTag)
        } else {
            context
        }
    }
}