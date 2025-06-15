package com.example.passman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecuritySetup
import com.example.passman.data.AppDatabase
import com.example.passman.security.EncryptionUtils
import com.example.passman.ui.theme.Amaranth
import com.example.passman.ui.theme.CarlsbergGreen
import kotlinx.coroutines.launch



@Composable
fun EditPasswordScreen(navController: NavController, passwordId: Int) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.passwordEntryDao()
    val scope = rememberCoroutineScope()
    val mek = SecuritySetup.SessionManager.getMEK() ?: return

    val passwordFlow = dao.getEntryById(passwordId).collectAsState(initial = null)
    val passwordEntry = passwordFlow.value

    var serviceName by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    // Update fields when passwordEntry is loaded
    LaunchedEffect(passwordEntry) {
        if (passwordEntry != null) {
            serviceName = passwordEntry.serviceName
            login = passwordEntry.login
            password = EncryptionUtils.decrypt(passwordEntry.password, mek)
            isFavorite = passwordEntry.isFavorite
        }
    }

    if (passwordEntry == null) {
        Text(stringResource(R.string.loading))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(stringResource(R.string.edit_password))
        MyOutlinedTextField(
            value = serviceName,
            onValueChange = { serviceName = it },
            label = stringResource(R.string.service_name)
        )
        MyOutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = stringResource(R.string.login)
        )
        MyOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFavorite, onCheckedChange = { isFavorite = it })
            Text(stringResource(R.string.favourite))
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            ElevatedButton(
                onClick = { navController.popBackStack() },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Amaranth
                )
            ) { Text(stringResource(R.string.cancel), color = Color.White) }
            ElevatedButton(onClick = {
                scope.launch {
                    val encryptedPassword = EncryptionUtils.encrypt(password, mek)
                    dao.update(passwordEntry.copy(serviceName = serviceName, login = login, password = encryptedPassword, isFavorite = isFavorite))
                    navController.popBackStack()
                }
            },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = CarlsbergGreen
                )
                ) { Text(stringResource(R.string.save), color = Color.White) }
        }
    }
}

@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    ) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Black,
            focusedPlaceholderColor = Color.Black

        )
    )
}