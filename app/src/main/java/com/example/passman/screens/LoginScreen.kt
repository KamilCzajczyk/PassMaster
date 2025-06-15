package com.example.passman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecurityLogin
import com.example.passman.navigation.Screen
import com.example.passman.ui.components.MyHeader1Text
import com.example.passman.ui.components.MyHeader3Text
import com.example.passman.ui.components.MyNextButton
import com.example.passman.ui.components.MyPasswordInput
import com.example.passman.ui.components.MyRecoveryButton
import com.example.passman.ui.theme.Amaranth


@Composable
fun LoginScreen(navController: NavController){
    var loginpassword by remember { mutableStateOf("") }
    var wrongPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {

        Icon(
            painter = painterResource(R.drawable.baseline_key_24),
            contentDescription = stringResource(R.string.logo),
            modifier = Modifier.size(118.dp)
        )
        MyHeader1Text(stringResource(R.string.welcome_back))
        MyHeader3Text(stringResource(R.string.please_provide_your_master_password))


        MyPasswordInput(
            stringResource(R.string.password),
            value = loginpassword,
            onValueChange = {loginpassword = it}
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        )  {
            if(wrongPassword){
                Text(stringResource(R.string.wrong_password), color = Amaranth, fontSize = 20.sp)
                Text(stringResource(R.string.access_denied), color = Amaranth, fontSize = 20.sp)
            }
            MyNextButton(oncClicked = {
                if(SecurityLogin(context).login(loginpassword)){
                    navController.navigate(Screen.PasswordList.route)
                } else {
                    wrongPassword = true
                }

            })
            MyRecoveryButton(navController)

        }
    }
}


