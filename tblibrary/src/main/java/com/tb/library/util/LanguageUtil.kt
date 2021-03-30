package com.tb.library.util

import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import com.tb.library.base.TbConfig
import java.util.*

object LanguageUtil {

    private var mSupportLanguages = TbConfig.getInstance().supportLanguages

    @Suppress("DEPRECATION")
    private fun applyLanguage(context: Context, newLanguage: String) {
        val resources = context.resources
        val configuration = resources.configuration
        val locale = getSupportLanguage(newLanguage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale)
        } else {
            // updateConfiguration
            configuration.locale = locale
            val dm = resources.displayMetrics
            resources.updateConfiguration(configuration, dm)
        }
    }

    fun attachBaseContext(
        context: Context,
        language: String
    ): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationResources(context, language)
        } else {
            applyLanguage(context, language)
            context
        }
    }

    private fun createConfigurationResources(
        context: Context,
        language: String
    ): Context {
        val resources = context.resources
        val configuration = resources.configuration
        val locale: Locale? = if (TextUtils.isEmpty(language)) { //如果没有指定语言使用系统首选语言
            systemPreferredLanguage
        } else { //指定了语言使用指定语言，没有则使用首选语言
            getSupportLanguage(language)
        }
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * 是否支持此语言
     *
     * @param language language
     * @return true:支持 false:不支持
     */
    fun isSupportLanguage(language: String): Boolean {
        return mSupportLanguages.containsKey(language)
    }

    /**
     * 获取支持语言
     *
     * @param language language
     * @return 支持返回支持语言，不支持返回系统首选语言
     */

    fun getSupportLanguage(language: String): Locale? {
        return if (isSupportLanguage(language)) {
            mSupportLanguages[language]
        } else systemPreferredLanguage
    }

    /**
     * 获取系统首选语言
     *
     * @return Locale
     */
    val systemPreferredLanguage: Locale
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList.getDefault()[0]
            } else {
                Locale.getDefault()
            }
        }
}