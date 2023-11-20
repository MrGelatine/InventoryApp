/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.item

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import java.nio.charset.Charset
import java.text.NumberFormat
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository, applicationContext: Context) : ViewModel() {

    val sharedPreferences = EncryptedSharedPreferences.create(
        "PreferencesFilename",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    fun generateKey(appName: String): SecretKey? {
        return SecretKeySpec(appName.toByteArray(), "AES")
    }
    fun decryptMsg(cipherText: ByteArray?, secret: SecretKey?): String? {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        var cipher: Cipher? = null
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secret)
        return String(cipher.doFinal(cipherText), Charset.defaultCharset())
    }
    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(
        ItemUiState(itemDetails = if(sharedPreferences.getBoolean("FillDefault", false))
            ItemDetails(provider_name = sharedPreferences.getString("ProviderDefault"," ") ?: " ", provider_email = sharedPreferences.getString("EmailDefault"," ") ?: " ", provider_phone = sharedPreferences.getString("PhoneDefault"," ") ?: " ")
        else ItemDetails()))
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }



    /**
     * Inserts an [Item] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank() && (provider_email.isBlank() or """\S+\@\S+\.(com|ru)""".toRegex().containsMatchIn(provider_email)) && (provider_phone.isBlank() || """(\d){9}""".toRegex().matches(provider_phone))
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val provider_name: String = "",
    val provider_email: String = "",
    val provider_phone: String = "",
    val source: String = "manual"
)

/**
 * Extension function to convert [ItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
    provider_name = provider_name,
    provider_email = provider_email,
    provider_phone = provider_phone,
    source = source
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    provider_name = provider_name,
    provider_email = provider_email,
    provider_phone = provider_phone,
    source = source
)
