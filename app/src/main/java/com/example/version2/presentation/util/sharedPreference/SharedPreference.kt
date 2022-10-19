package com.example.version2.presentation.util.sharedPreference

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.presentation.homeScreen.enums.SortValues


class SharedPreference(private val activity: Activity) {





    private val preference: SharedPreferences by lazy {
        activity.getSharedPreferences(
            Keys.MAIN,
            Context.MODE_PRIVATE
        )
    }


    fun getSharedPreferenceInt(value: String): Int {
        return preference.getInt(value, Keys.LIST_VIEW)
    }

    fun getSharedPreferenceString(value: String): String? {
        return preference.getString(value, SortValues.ALPHABETICALLY_TITLE.toString())
    }

    fun putIntSharePreferenceInt(string: String, value: Int) {
        preference.edit().apply()
        {
            putInt(string, value)
            apply()
        }

    }

    fun putStringSharedPreference(string: String, value: String) {
        preference.edit().apply()
        {
            putString(string, value)
            apply()
        }
    }


    fun putBooleanSharedPreference(string: String, value: Boolean) {
        preference.edit().apply {
            putBoolean(string, value)
            apply()
        }
    }

    fun getBooleanSharedPreference(string: String): Boolean {
        return preference.getBoolean(string, false)
    }


}