package com.shihab.practicesharedprefarence.ui.screen.settingscreen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import com.shihab.practicesharedprefarence.data.PreferenceManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val pref = PreferenceManager(application)

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    private val _currency = mutableStateOf("৳")
    val currency: State<String> = _currency

    init {
        loadData()
    }

    private fun loadData() {
        _userName.value = pref.getUserName()
        _isDarkMode.value = pref.isDarkMode()
        _currency.value = pref.getCurrency()
    }

    fun saveUserName(name: String) {
        _userName.value = name
        pref.saveUserName(name)
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        pref.setDarkMode(isDark)
    }

    fun saveCurrency(symbol: String) {
        _currency.value = symbol
        pref.saveCurrency(symbol)
    }
}
