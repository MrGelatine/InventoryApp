package com.example.inventory.ui.SettingsScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.item.ItemDetailsDestination
import com.example.inventory.ui.item.ItemDetailsViewModel
import com.example.inventory.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.item_detail_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Card(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            var providerDefault by remember {mutableStateOf(viewModel.sharedPreferences.getString("ProviderDefault", "")) }
            var emailDefault by remember {mutableStateOf(viewModel.sharedPreferences.getString("EmailDefault", "")) }
            var phoneDefault by remember {mutableStateOf(viewModel.sharedPreferences.getString("PhoneDefault", "")) }
            var checkedFillDefault by remember{mutableStateOf(viewModel.sharedPreferences.getBoolean("FillDefault", false))}
            var checkedHideSensitive by remember{mutableStateOf(viewModel.sharedPreferences.getBoolean("Hide", false))}
            var checkedForbidShare by remember{mutableStateOf(viewModel.sharedPreferences.getBoolean("Forbid", false))}
            OutlinedTextField(
                value = providerDefault!!,
                onValueChange = { providerDefault = it
                        viewModel.sharedPreferences.edit().putString("ProviderDefault", it).apply()
                                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text(stringResource(R.string.provider_default)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.padding(all = 10.dp).fillMaxWidth(),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = emailDefault!!,
                onValueChange = { emailDefault = it
                    viewModel.sharedPreferences.edit().putString("EmailDefault", it).apply()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text(stringResource(R.string.email_default)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.padding(all = 10.dp).fillMaxWidth(),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = phoneDefault!!,
                onValueChange = { phoneDefault = it
                    viewModel.sharedPreferences.edit().putString("PhoneDefault", it).apply()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = { Text(stringResource(R.string.phone_default)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.padding(all = 10.dp).fillMaxWidth(),
                enabled = true,
                singleLine = true
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 10.dp)){
                Text(text = stringResource(R.string.settings_fill_default), modifier = Modifier.padding(end = 10.dp))
                Switch(
                    checked = checkedFillDefault,
                    onCheckedChange = {
                        viewModel.sharedPreferences.edit().putBoolean("FillDefault", it).apply()
                        checkedFillDefault = it
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 10.dp)){
                Text(text = stringResource(R.string.settings_hide_sensitive), modifier = Modifier.padding(end = 10.dp))
                Switch(
                    checked = checkedHideSensitive,
                    onCheckedChange = {
                        viewModel.sharedPreferences.edit().putBoolean("Hide", it).apply()
                        checkedHideSensitive = it
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 10.dp)){
                Text(text = stringResource(R.string.settings_forbid_share), modifier = Modifier.padding(end = 10.dp))
                Switch(
                    checked =  checkedForbidShare,
                    onCheckedChange = {
                        viewModel.sharedPreferences.edit().putBoolean("Forbid", it).apply()
                        checkedForbidShare = it
                    }
                )
            }
        }
    }
}