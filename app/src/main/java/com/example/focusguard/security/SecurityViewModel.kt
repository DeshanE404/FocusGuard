package com.example.focusguard.security

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application)
    val state: StateFlow<SecurityState?> = repository.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun saveCredential(value: String, type: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val (salt, hash) = SecureHash.create(value)
            repository.saveCredential(salt, hash, type)
            onComplete()
        }
    }

    fun verify(value: String, now: Long = System.currentTimeMillis(), onResult: (Boolean, Long) -> Unit) {
        viewModelScope.launch {
            val current = repository.current()
            if (!current.enabled || current.salt == null || current.hash == null) {
                onResult(true, 0)
                return@launch
            }
            if (current.lockoutUntil > now) {
                onResult(false, current.lockoutUntil)
                return@launch
            }
            if (SecureHash.matches(value, current.salt, current.hash)) {
                repository.clearFailures()
                onResult(true, 0)
            } else {
                val attempts = current.failedAttempts + 1
                val lockout = if (attempts >= 5) now + 30_000 else 0
                repository.recordFailure(now, lockout)
                onResult(false, lockout)
            }
        }
    }

    fun disable(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.disable()
            onComplete()
        }
    }
}
