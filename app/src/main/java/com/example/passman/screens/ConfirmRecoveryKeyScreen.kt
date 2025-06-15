package com.example.passman.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.navigation.Screen
import com.example.passman.ui.components.MyHeader3Text
import com.example.passman.ui.components.MyNextButton
import com.example.passman.ui.components.MyRecoveryKeyInput
import com.example.passman.ui.theme.Amaranth


@Composable
fun ConfirmRecoveryKeyScreen(navController: NavController, recoveryKey: String){
    ConfirmRecoveryKey(navController, recoveryKey)
}

@Composable
fun ConfirmRecoveryKey(navController: NavController, recoveryKey: String){
    val context = LocalContext.current
    var recoveryKeyinput by remember { mutableStateOf("") }
    var wrongcode by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        MyHeader3Text(stringResource(R.string.please_provide_recovery_key_to_confirm_the_setup))
        MyRecoveryKeyInput { input-> recoveryKeyinput = input }
        if(wrongcode){
            Text(stringResource(R.string.wrong_recovery_key), color = Amaranth)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyNextButton(oncClicked = {

                if(recoveryKeyinput == recoveryKey){

                    val sharedPreferences = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("is_first_login_completed", true).apply()
                    navController.navigate(Screen.PasswordList.route)
                } else {
                    wrongcode = true
                }
            })
        }
    }
}


