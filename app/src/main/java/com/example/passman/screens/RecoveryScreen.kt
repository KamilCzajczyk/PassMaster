package com.example.passman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecuritySetup
import com.example.passman.navigation.Screen
import com.example.passman.ui.components.MyHeader1Text
import com.example.passman.ui.components.MyHeader3Text
import com.example.passman.ui.components.MyNextButton
import com.example.passman.ui.components.MyPasswordInput
import com.example.passman.ui.components.MyRecoveryKeyInput
import com.example.passman.ui.theme.Amaranth


@Composable
fun RecoveryScreen(navController: NavController) {
    Recovery(navController)
}

@Composable
fun Recovery(navController: NavController){
    val context = LocalContext.current
    val securitySetup = SecuritySetup(context)
    var wrongcode by remember { mutableStateOf(false) }
    var recoveryKey by remember { mutableStateOf("") }
    var newMasterPassword by remember { mutableStateOf("") }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ){
        MyHeader1Text(stringResource(R.string.password_recovery))
        MyHeader3Text(stringResource(R.string.please_provide_recovery_key))


        MyRecoveryKeyInput {input -> recoveryKey = input}
//        OutlinedTextField(
//
//            value = newMasterPassword,
//            onValueChange = {
//                newMasterPassword = it
//            },
//            label = { Text("New master password") },
//            modifier = Modifier.fillMaxWidth().padding(16.dp)
//        )
        MyPasswordInput(stringResource(R.string.new_master_password), newMasterPassword, onValueChange = {newMasterPassword = it})
        if(wrongcode){
            Text(stringResource(R.string.wrong_recovery_key_2), color = Amaranth)
        }
        MyNextButton(oncClicked = {
            val isValid = securitySetup.recoverWithRecoveryKey(recoveryKey, newMasterPassword)
            if(isValid){
                navController.navigate(Screen.PasswordList.route)
            } else {
                wrongcode = true
            }
        })
    }
}

