package com.example.cinemahelper.utils

import android.content.Context
import androidx.core.os.ConfigurationCompat

object LocaleChecker {
    fun isRussianLocale(context: Context): Boolean{
        return ConfigurationCompat.getLocales(context.resources.configuration)[0].language == "ru"
    }
}