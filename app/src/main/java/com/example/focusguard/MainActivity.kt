package com.example.focusguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusguard.security.SecurityViewModel
import com.example.focusguard.ui.security.LockScreen
import com.example.focusguard.ui.security.SecurityScreen
import com.example.focusguard.ui.settings.SettingsScreen
import com.example.focusguard.ui.apptimelimits.AppTimeLimitsScreen
import com.example.focusguard.ui.home.HomeScreen
import com.example.focusguard.ui.theme.FocusGuardTheme

class MainActivity : ComponentActivity() {
    private var showAppTimeLimits by mutableStateOf(false)
    private var showSecurity by mutableStateOf(false)
    private var showSettings by mutableStateOf(false)
    private var settingsRefreshKey by mutableStateOf(0)
    private var unlocked by mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        settingsRefreshKey++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val securityViewModel: SecurityViewModel = viewModel()
            val securityState by securityViewModel.state.collectAsState()
            securityState?.let { currentState ->
                FocusGuardTheme {
                    when {
                        currentState.enabled && !unlocked -> LockScreen(securityViewModel) { unlocked = true }
                        showSecurity -> SecurityScreen(onBack = { showSecurity = false }, viewModel = securityViewModel)
                        showSettings -> SettingsScreen(onBack = { showSettings = false }, refreshKey = settingsRefreshKey)
                        showAppTimeLimits -> AppTimeLimitsScreen(onBack = { showAppTimeLimits = false })
                        else -> HomeScreen(
                            onAppTimeLimitsClick = { showAppTimeLimits = true },
                            onSecurityClick = { showSecurity = true },
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
