package com.flow.mailflow.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferenceHelper(private val mContext: Context = App.applicationContext()) {

    fun putInt(key: String?, value: Int): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putInt(key, value)
        edit.apply()
        return this
    }


    fun putBoolean(key: String?, `val`: Boolean): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putBoolean(key, `val`)
        edit.apply()
        return this
    }

    fun putString(key: String?, `val`: String?): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putString(key, `val`)
        edit.apply()
        return this
    }

    fun putFloat(key: String?, `val`: Float): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putFloat(key, `val`)
        edit.apply()
        return this
    }

    fun putLong(key: String?, `val`: Long): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        val edit = preferences.edit()
        edit.putLong(key, `val`)
        edit.apply()
        return this
    }

    fun getLong(key: String?, _default: Long): Long {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getLong(key, _default)
    }

    fun getFloat(key: String?, _default: Float): Float {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getFloat(key, _default)
    }

    fun getString(key: String?, _default: String): String {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getString(key, _default)?:_default
    }
    fun getString(key: String?): String {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getString(key, "0")?:"0"
    }

    fun getInt(key: String?, _default: Int): Int {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getInt(key, _default)
    }

    fun getBoolean(key: String?, _default: Boolean =false): Boolean {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.getBoolean(key, _default)
    }

    fun hasKey(key: String?): Boolean {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        return preferences.contains(key)
    }

    fun clearPreferences(): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        preferences.edit().clear().apply()
        return this
    }

    fun clearPreferences(key: String?): SharedPreferenceHelper {
        val preferences: SharedPreferences = android.preference.PreferenceManager
            .getDefaultSharedPreferences(mContext)
        preferences.edit().remove(key).apply()
        return this
    }


    companion object {

        const val TOKEN = "token"

        fun with(context: Context): SharedPreferenceHelper {
            return SharedPreferenceHelper(context)
        }
    }

}