package com.shihab.practicesharedprefarence

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.shihab.practicesharedprefarence.data.PreferenceManager
import com.shihab.practicesharedprefarence.ui.screen.chartscreen.ChartScreen
import com.shihab.practicesharedprefarence.ui.screen.expensescreen.ExpenseScreen
import com.shihab.practicesharedprefarence.ui.screen.expensescreen.ExpenseViewModel
import com.shihab.practicesharedprefarence.ui.screen.settingscreen.SettingsScreen
import com.shihab.practicesharedprefarence.ui.screen.settingscreen.SettingsViewModel
import com.shihab.practicesharedprefarence.ui.theme.PracticeSharedPrefarenceTheme

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val preferenceManager = PreferenceManager(applicationContext)
        
        setContent {
            val settingsViewModel = SettingsViewModel(application)
            val isDarkMode by settingsViewModel.isDarkMode
            var isUnlocked by remember { mutableStateOf(!preferenceManager.isBiometricEnabled()) }

            PracticeSharedPrefarenceTheme(darkTheme = isDarkMode) {
                if (isUnlocked) {
                    MainContent(settingsViewModel)
                } else {
                    LaunchedEffect(Unit) {
                        showBiometricPrompt { success ->
                            isUnlocked = success
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Optional: Add a "Unlock" button here if prompt is dismissed
                    }
                }
            }
        }
    }

    @Composable
    private fun MainContent(settingsViewModel: SettingsViewModel) {
        val expenseViewModel = ExpenseViewModel(application)
        var currentScreen by remember { mutableStateOf("home") }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "chart",
                        onClick = { currentScreen = "chart" },
                        icon = { Icon(Icons.Default.Info, contentDescription = "Chart") },
                        label = { Text("Analytics") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "settings",
                        onClick = { currentScreen = "settings" },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    "home" -> ExpenseScreen(viewModel = expenseViewModel)
                    "chart" -> ChartScreen(viewModel = expenseViewModel)
                    "settings" -> SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }

    private fun showBiometricPrompt(onResult: (Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Auth error: $errString", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Auth failed", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
