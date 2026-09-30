package com.example.shopping.core.session

import platform.Foundation.NSUserDefaults

class IosSessionStorage : SessionStorage {

    private val defaults =
        NSUserDefaults.standardUserDefaults

    override fun putString(
        key: String,
        value: String
    ) {
        defaults.setObject(
            value,
            forKey = key
        )
    }

    override fun getString(
        key: String
    ): String? {
        return defaults.stringForKey(key)
    }

    override fun putLong(
        key: String,
        value: Long
    ) {
        defaults.setDouble(
            value.toDouble(),
            forKey = key
        )
    }

    override fun getLong(
        key: String
    ): Long? {
        if (defaults.objectForKey(key) == null) {
            return null
        }

        return defaults
            .doubleForKey(key)
            .toLong()
    }

    override fun remove(
        key: String
    ) {
        defaults.removeObjectForKey(key)
    }
}