package com.example.passman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.passman.data.PasswordEntry
import com.example.passman.navigation.Screen
import com.example.passman.security.EncryptionUtils
import com.example.passman.security.generateSecurePassword
import com.example.passman.ui.components.MyHeader1Text
import com.example.passman.ui.theme.Amaranth
import com.example.passman.ui.theme.CarlsbergGreen
import com.example.passman.ui.theme.CoolGray
import kotlinx.coroutines.launch


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewPasswordAddScreen() {
//    PasswordAddScreen()
//}

@Composable
fun PasswordAddScreen(navController: NavController) {

    val db = AppDatabase.getDatabase(context = LocalContext.current)
    val dao = db.passwordEntryDao()
    val scope = rememberCoroutineScope()
    //val checkedState = remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var serviceName by remember { mutableStateOf("") }
    var newLogin by remember { mutableStateOf("") }
    var newFav = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
    ) {
        MyHeader1Text(stringResource(R.string.add_new_password))
        MyOutlinedTextField2(
            value = serviceName,
            onValueChange = { serviceName = it },
            label = stringResource(R.string.service_name),
            
        )
        MyOutlinedTextField2(
            value = newLogin,
            onValueChange = { newLogin = it},
            label = stringResource(R.string.login),

        )
        MyOutlinedTextField2(
            value = newPassword,
            onValueChange = {newPassword = it},
            label = stringResource(R.string.password),
        )

        ElevatedButton(
            onClick = {
                newPassword = generateSecurePassword()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = CoolGray,
                contentColor = Color.White
            )
        ) {
            Text(stringResource(R.string.generate_secure_password))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = newFav.value, onCheckedChange = {newFav.value = it})
            Text(stringResource(R.string.favourite))
        }

        if(newPassword.isEmpty() || serviceName.isEmpty() || newLogin.isEmpty()){
            Text(stringResource(R.string.fill_all_fields), color = Color.Red)
        }


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ElevatedButton(
                onClick = {
                    navController.navigate(Screen.PasswordList.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Amaranth,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.cancel))
            }

            ElevatedButton(
                onClick = {
                    val mek = SecuritySetup.SessionManager.getMEK() ?: return@ElevatedButton
                    scope.launch {

                        if(serviceName.isNotEmpty() && newLogin.isNotEmpty() && newPassword.isNotEmpty()){
                            val encryptedPassword = EncryptionUtils.encrypt(newPassword, mek)
                            dao.insert(PasswordEntry(serviceName = serviceName, login = newLogin, password = encryptedPassword, isFavorite = newFav.value))
                            navController.navigate(Screen.PasswordList.route)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CarlsbergGreen,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.confirm))
            }
        }


    }
}

@Composable
fun MyOutlinedTextField2(
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

        ),
        maxLines = 1
    )
}

