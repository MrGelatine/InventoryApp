package com.example.inventory.ui.SettingsScreen

import android.R.attr.password
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class SettingsScreenViewModel(applicationContext: Context) : ViewModel(){
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "PreferencesFilename",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


}