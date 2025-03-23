package com.flow.mailflow.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferenceHelper (private val mContext: Context = App.applicationContext())  {

    fun putInt(key: String?, value: Int): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().putInt(key, value).apply()
        return this
    }
    fun putBoolean(key: String?, `val`: Boolean): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().putBoolean(key, `val`).apply()
        return this
    }

    fun putString(key: String?, `val`: String?): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().putString(key, `val`).apply()
        return this
    }
    fun putFloat(key: String?, `val`: Float): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().putFloat(key, `val`).apply()
        return this
    }




    fun getInt(key: String?, default: Int): Int {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return preferences.getInt(key, default)
    }

    fun getString(key: String?, default: String): String {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return preferences.getString(key, default)?:default
    }

    fun getBoolean(key: String?, default: Boolean =false): Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return preferences.getBoolean(key, default)
    }

    fun getFloat(key: String?, default: Float): Float {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return preferences.getFloat(key, default)
    }

    fun clearPreferences(): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().clear().apply()
        return this
    }

    fun clearPreferences(key: String?): SharedPreferenceHelper {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        preferences.edit().remove(key).apply()
        return this
    }


    companion object {
        const val TOKEN = "token"
        const val IS_LOGGED="is_logged"






        fun with(context: Context): SharedPreferenceHelper {
            return SharedPreferenceHelper(context)
        }
    }


}