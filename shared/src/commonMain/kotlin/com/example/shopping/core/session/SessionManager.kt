package com.example.shopping.core.session

import kotlin.random.Random
import kotlin.time.Clock

class SessionManager(
    private val storage: SessionStorage
) {

    companion object {
        private const val SESSION_ID_KEY = "session_id"
        private const val SESSION_EXPIRY_KEY = "session_expiry"

        private const val SESSION_DURATION_MS =
            60 * 60 * 1000L
    }

    fun createSession() {
        val sessionId = generateSessionId()

        val expiresAt =
            Clock.System.now()
                .toEpochMilliseconds() +
                    SESSION_DURATION_MS

        storage.putString(
            key = SESSION_ID_KEY,
            value = sessionId
        )

        storage.putLong(
            key = SESSION_EXPIRY_KEY,
            value = expiresAt
        )
    }

    fun isSessionValid(): Boolean {
        val sessionId =
            storage.getString(SESSION_ID_KEY)

        val expiresAt =
            storage.getLong(SESSION_EXPIRY_KEY)

        if (sessionId.isNullOrBlank()) {
            return false
        }

        if (expiresAt == null) {
            clearSession()
            return false
        }

        val now =
            Clock.System.now()
                .toEpochMilliseconds()

        if (now >= expiresAt) {
            clearSession()
            return false
        }

        return true
    }

    fun getSessionId(): String? {
        if (!isSessionValid()) {
            return null
        }

        return storage.getString(SESSION_ID_KEY)
    }

    fun clearSession() {
        storage.remove(SESSION_ID_KEY)
        storage.remove(SESSION_EXPIRY_KEY)
    }

    private fun generateSessionId(): String {
        val characters =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        return buildString {
            repeat(32) {
                append(
                    characters[
                        Random.nextInt(characters.length)
                    ]
                )
            }
        }
    }
}