package com.example.focusguard.blocking

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusguard.monitoring.MonitoringRepository
import com.example.focusguard.security.SecurityViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusguard.ui.theme.FocusGuardTheme

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME).orEmpty()
        val appName = intent.getStringExtra(EXTRA_APP_NAME).orEmpty()
        val used = intent.getIntExtra(EXTRA_USED_MINUTES, 0)
        val limit = intent.getIntExtra(EXTRA_LIMIT_MINUTES, 0)
        setContent {
            val securityViewModel: SecurityViewModel = viewModel()
            val unlocks = remember { MonitoringRepository(applicationContext) }
            var input by remember { mutableStateOf("") }
            var error by remember { mutableStateOf<String?>(null) }
            var authenticated by remember { mutableStateOf(false) }
            FocusGuardTheme {
                BackHandler(enabled = true) {}
                Column(
                    Modifier.fillMaxSize().background(Color(0xFF171717)).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("$appName blocked", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Daily limit reached", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Time used today: $used min", color = Color.White)
                    Text("Daily limit: $limit min", color = Color.White)
                    if (!authenticated) {
                        OutlinedTextField(input, { input = it; error = null }, label = { Text("FocusGuard PIN or password") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { securityViewModel.verify(input) { valid, _ -> if (valid) authenticated = true else error = "Incorrect PIN or password" } }, modifier = Modifier.fillMaxWidth()) { Text("Verify") }
                        error?.let { Text(it, color = Color(0xFFFF8A80)) }
                    } else {
                        Button(onClick = { lifecycleScope.launch { unlocks.unlockUntil(packageName, System.currentTimeMillis() + 300_000); finishAndRemoveTask() } }, modifier = Modifier.fillMaxWidth()) { Text("Unlock for 5 minutes") }
                        Button(onClick = {
                            startActivity(Intent(this@BlockActivity, com.example.focusguard.MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            })
                            finishAndRemoveTask()
                        }, modifier = Modifier.fillMaxWidth()) { Text("Return to FocusGuard") }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_APP_NAME = "app_name"
        const val EXTRA_USED_MINUTES = "used_minutes"
        const val EXTRA_LIMIT_MINUTES = "limit_minutes"
    }
}
