package com.example.focusguard.security

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SecurityRepository(context: Context) {
    private val db = SecurityDb(context.applicationContext)
    private val stateFlow = MutableStateFlow(read())
    val state: Flow<SecurityState> = stateFlow

    private fun read(): SecurityState = db.readableDatabase.query("security", null, "id = 1", null, null, null, null).use { cursor ->
        if (!cursor.moveToFirst()) return SecurityState()
        SecurityState(cursor.getInt(1) != 0, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getLong(6))
    }

    private fun update(block: ContentValues.() -> Unit) {
        val values = ContentValues().apply(block)
        db.writableDatabase.update("security", values, "id = 1", null)
        stateFlow.value = read()
    }

    fun saveCredential(salt: String, hash: String, type: String) = update {
        put("enabled", 1); put("salt", salt); put("hash", hash); put("credential_type", type); put("failed_attempts", 0); put("lockout_until", 0)
    }
    fun disable() = update { put("enabled", 0); putNull("salt"); putNull("hash"); put("failed_attempts", 0); put("lockout_until", 0) }
    fun recordFailure(now: Long, lockoutUntil: Long) = update {
        put("failed_attempts", if (lockoutUntil > now) 0 else stateFlow.value.failedAttempts + 1); put("lockout_until", lockoutUntil)
    }
    fun clearFailures() = update { put("failed_attempts", 0); put("lockout_until", 0) }
    fun current(): SecurityState = stateFlow.value
}

private class SecurityDb(context: Context) : SQLiteOpenHelper(context, "security.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE security (id INTEGER PRIMARY KEY, enabled INTEGER NOT NULL, salt TEXT, hash TEXT, credential_type TEXT NOT NULL, failed_attempts INTEGER NOT NULL, lockout_until INTEGER NOT NULL)")
        db.execSQL("INSERT INTO security VALUES (1, 0, NULL, NULL, 'PIN', 0, 0)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE security RENAME TO security_old")
            onCreate(db)
            db.execSQL("UPDATE security SET enabled = (SELECT enabled FROM security_old WHERE id = 1), salt = (SELECT salt FROM security_old WHERE id = 1), hash = (SELECT hash FROM security_old WHERE id = 1), failed_attempts = (SELECT failed_attempts FROM security_old WHERE id = 1), lockout_until = (SELECT lockout_until FROM security_old WHERE id = 1) WHERE id = 1")
            db.execSQL("DROP TABLE security_old")
        }
    }
}

data class SecurityState(
    val enabled: Boolean = false,
    val salt: String? = null,
    val hash: String? = null,
    val credentialType: String = "PIN",
    val failedAttempts: Long = 0,
    val lockoutUntil: Long = 0
)

