package com.example.passman.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecuritySetup
import com.example.passman.navigation.Screen
import com.example.passman.ui.components.MyHeader1Text
import com.example.passman.ui.components.MyHeader3Text
import com.example.passman.ui.components.MyNextButton
import com.example.passman.ui.components.MyPasswordInput
import com.example.passman.ui.theme.Amaranth
import com.example.passman.ui.theme.CarlsbergGreen


@Composable
fun FirstLaunchScreen(navController: NavController) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    // requirements
    val meetsLengthRequirement = password.length >= 6
    val meetsDigitRequirement = password.any { it.isDigit() }
    val meetsSpecialCharRequirement = password.any { it in "!@#$%^&*()_+{}[]|:;<>,.?/~`" }
    val meetsUppercaseRequirement = password.any { it.isUpperCase() }
    val meetsLowercaseRequirement = password.any { it.isLowerCase() }
    val passwordsMatch = passwordsMatch(password, confirmPassword)

    val allRequirementsMet = meetsLengthRequirement &&
            meetsDigitRequirement &&
            meetsSpecialCharRequirement &&
            meetsUppercaseRequirement &&
            meetsLowercaseRequirement &&
            passwordsMatch

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_key_24),
            contentDescription = "logo",
            modifier = Modifier.size(118.dp)
        )
        MyHeader1Text(stringResource(R.string.welcome_to_passmaster))
        MyHeader3Text(stringResource(R.string.please_provide_master_password))
        MyPasswordInput(
            stringResource(R.string.password),
            value = password,
            onValueChange = { password = it }
        )

        PasswordRequirements(
            meetsLengthRequirement = meetsLengthRequirement,
            meetsDigitRequirement = meetsDigitRequirement,
            meetsSpecialCharRequirement = meetsSpecialCharRequirement,
            meetsUppercaseRequirement = meetsUppercaseRequirement,
            meetsLowercaseRequirement = meetsLowercaseRequirement,
            passwordsMatch = passwordsMatch
        )

        MyPasswordInput(
            stringResource(R.string.confirm_password),
            value = confirmPassword,
            onValueChange = { confirmPassword = it })


        MyNextButton(
            oncClicked = {
                val passed = password

                if (allRequirementsMet) {
                    Log.d("allmet", "allmet")

                   val securitySetup = SecuritySetup(context)

                    try {
                        val success = securitySetup.setupFirstLogin(passed)
                        if(success){
                            Log.d("FirstLaunch", "First login setup completed successfully")
                            val recoveryKey = securitySetup.setupRecoveryKey()

                            navController.navigate(Screen.RecoveryKey.route + "/$recoveryKey")
                        } else {
                            Log.e("FirstLaunch", "Failed to complete first login setup")
                            Toast.makeText(
                                context,
                                "Failed to setup security. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }catch (e: Exception){
                        Log.e("FirstLaunch", "Error during first login setup", e)
                        Toast.makeText(
                            context,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    // END TESTOW


                    //navController.navigate(Screen.PasswordList.route)



                }
            }
        )

    }
}

@Composable
fun PasswordRequirements(
    meetsLengthRequirement: Boolean,
    meetsDigitRequirement: Boolean,
    meetsSpecialCharRequirement: Boolean,
    meetsUppercaseRequirement: Boolean,
    meetsLowercaseRequirement: Boolean,
    passwordsMatch: Boolean
) {
    Column {
        RequirementRow(text = stringResource(R.string.at_least_6_characters), isMet = meetsLengthRequirement)
        RequirementRow(text = stringResource(R.string.at_least_one_digit), isMet = meetsDigitRequirement)
        RequirementRow(text = stringResource(R.string.at_least_one_special_character), isMet = meetsSpecialCharRequirement)
        RequirementRow(text = stringResource(R.string.at_least_one_uppercase_letter), isMet = meetsUppercaseRequirement)
        RequirementRow(text = stringResource(R.string.at_least_one_lowercase_letter), isMet = meetsLowercaseRequirement)
        RequirementRow(text = stringResource(R.string.passwords_match), isMet = passwordsMatch)
    }
}


@Composable
fun RequirementRow(text: String, isMet: Boolean) {
    val color = if (isMet) CarlsbergGreen else Amaranth
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = if (isMet) painterResource(id = R.drawable.baseline_check_circle_outline_24) else painterResource(
                id = R.drawable.baseline_cancel_24
            ),
            contentDescription = if (isMet) "Met" else "Not Met",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color)
    }
}


fun passwordsMatch(password: String, confirmPassword: String): Boolean {
    return if (password.isEmpty() && confirmPassword.isEmpty()) {
        false
    } else {
        password == confirmPassword
    }
}


