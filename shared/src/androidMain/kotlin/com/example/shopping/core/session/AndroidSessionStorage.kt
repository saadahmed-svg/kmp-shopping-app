package com.example.shopping.core.session

import android.content.Context
import android.content.SharedPreferences

class AndroidSessionStorage(
    context: Context
) : SessionStorage {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(
            "shopping_session",
            Context.MODE_PRIVATE
        )

    override fun putString(
        key: String,
        value: String
    ) {
        preferences.edit()
            .putString(key, value)
            .apply()
    }

    override fun getString(
        key: String
    ): String? {
        return preferences.getString(
            key,
            null
        )
    }

    override fun putLong(
        key: String,
        value: Long
    ) {
        preferences.edit()
            .putLong(key, value)
            .apply()
    }

    override fun getLong(
        key: String
    ): Long? {
        if (!preferences.contains(key)) {
            return null
        }

        return preferences.getLong(
            key,
            0L
        )
    }

    override fun remove(
        key: String
    ) {
        preferences.edit()
            .remove(key)
            .apply()
    }
}