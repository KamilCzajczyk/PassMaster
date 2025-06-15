package com.example.passman.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.navigation.Screen

@Composable
fun MyRecoveryButton(navController: NavController){

    Button(
        onClick = {
            navController.navigate(Screen.Recovery.route)

        },
        modifier = Modifier.size(width = 160.dp, height = 55.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        elevation = ButtonDefaults.buttonElevation(10.dp)
    ) {
        Row (
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
        ){
            Icon(
                painter = painterResource(R.drawable.baseline_warning_24),
                contentDescription = stringResource(R.string.danger),
                tint = Color.White
            )
            Text(text = stringResource(R.string.password_recovery),
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}