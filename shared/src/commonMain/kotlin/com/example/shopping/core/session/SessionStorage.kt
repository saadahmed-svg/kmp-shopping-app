package com.example.shopping.core.session

interface SessionStorage {

    fun putString(
        key: String,
        value: String
    )

    fun getString(
        key: String
    ): String?

    fun putLong(
        key: String,
        value: Long
    )

    fun getLong(
        key: String
    ): Long?

    fun remove(
        key: String
    )
}