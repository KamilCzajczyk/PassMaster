package com.example.passman.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecurityLogin
import com.example.passman.SecuritySetup
import com.example.passman.data.AppDatabase
import com.example.passman.data.PasswordEntry
import com.example.passman.navigation.Screen
import com.example.passman.security.EncryptionUtils
import com.example.passman.ui.theme.Amaranth
import com.example.passman.ui.theme.CandyAppleRed
import com.example.passman.ui.theme.FederalBlue
import com.example.passman.ui.theme.CarlsbergGreen
import com.example.passman.ui.theme.Cinnabar
import com.example.passman.ui.theme.HonoluluBlue
import com.example.passman.ui.theme.LightCyan
import com.example.passman.ui.theme.MarianBlue
import com.example.passman.ui.theme.Persimon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

@Composable
fun DataBreachScreen(navController: NavController) {
    DataBreach(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataBreach(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context = context)
    val dao = db.passwordEntryDao()
    val mek = SecuritySetup.SessionManager.getMEK() ?: return
    val scope = rememberCoroutineScope()

    var breached by remember { mutableStateOf<List<PasswordEntry>?>(emptyList()) }
    var checking by remember { mutableStateOf(false) }
    var rateLimited by remember { mutableStateOf(false) }
    var networkError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.data_breach_check), color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FederalBlue
                ),
                actions = {
                    IconButton(onClick = {
                        SecurityLogin(context).logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = stringResource(R.string.logout),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = MarianBlue,
        bottomBar = {
            NavigationBar(
                containerColor = FederalBlue
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.PasswordList.route) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.baseline_format_list_bulleted_24),
                            contentDescription = stringResource(R.string.passwords)
                        )
                    },
                    label = { Text(stringResource(R.string.passwords), color = Color.White) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(Screen.DataBreach.route) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.outline_cloud_lock_24),
                            contentDescription = stringResource(R.string.data_breach)
                        )
                    },
                    label = { Text(stringResource(R.string.data_breach),color = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = {
                    checking = true
                    rateLimited = false
                    networkError = false
                    breached = emptyList()
                    scope.launch {
                        val allEntries = dao.getAll()
                        val breachedList = mutableListOf<PasswordEntry>()
                        for (entry in allEntries) {
                            val decrypted = EncryptionUtils.decrypt(entry.password, mek)
                            when (val result = isPasswordPwned(decrypted)) {
                                is PwnedResult.Breached -> breachedList.add(entry)
                                is PwnedResult.RateLimited -> {
                                    rateLimited = true
                                    break
                                }
                                is PwnedResult.NetworkError -> {
                                    networkError = true
                                    break
                                }
                                else -> {}
                            }
                        }
                        breached = breachedList
                        checking = false
                    }
                },
                enabled = !checking,
                colors = ButtonDefaults.buttonColors(containerColor = HonoluluBlue),
                elevation = ButtonDefaults.buttonElevation(5.dp)
            ) {
                Text(stringResource(R.string.check_for_breached_passwords), color= Color.White)
            }

            if (checking) {
                //Text("Checking...", color = Color.Gray)
                CircularProgressIndicator(
                    color = LightCyan,
                    modifier = Modifier.padding(16.dp)
                )
            }else if (networkError) {
                Text(stringResource(R.string.no_internet_connection_or_network_error), color = Cinnabar)
            } else if (rateLimited) {
                Text(stringResource(R.string.rate_limit_reached_please_try_again_later), color = Amaranth)
            } else if (breached != null && breached!!.isNotEmpty()) {
                Text(stringResource(R.string.breached_password_found_for_service), color = Cinnabar)
                breached!!.forEach {
                    Text("- ${it.serviceName} (${it.login})", color = Cinnabar)
//                    Card {
//                        Column {
//                            Text(text = "Service name: ${it.serviceName}", color = Color.Black)
//                            Text(text = "Login: ${it.login}", color = Color.Black)
//                            Spacer(modifier = Modifier.weight(1f))
//                        }
//                    }
                }
            } else if (breached != null && breached!!.isEmpty()) {
                Text(stringResource(R.string.no_breached_passwords_found), color = LightCyan)
            }
        }
    }
}

sealed class PwnedResult {
    data object Breached : PwnedResult()
    data object NotBreached : PwnedResult()
    data object RateLimited : PwnedResult()
    data object NetworkError : PwnedResult()
    data object UnknownError : PwnedResult()
}


suspend fun isPasswordPwned(password: String): PwnedResult = withContext(Dispatchers.IO) {
    try {
        val sha1 = MessageDigest.getInstance("SHA-1")
            .digest(password.toByteArray())
            .joinToString("") { "%02X".format(it) }
        val prefix = sha1.substring(0, 5)
        val suffix = sha1.substring(5)
        val url = URL("https://api.pwnedpasswords.com/range/$prefix")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        try {
            val responseCode = connection.responseCode
            when (responseCode) {
                429 -> return@withContext PwnedResult.RateLimited
                200 -> {
                    val found = connection.inputStream.bufferedReader().useLines { lines ->
                        lines.any { it.startsWith(suffix, ignoreCase = true) }
                    }
                    return@withContext if (found) PwnedResult.Breached else PwnedResult.NotBreached
                }
                else -> return@withContext PwnedResult.UnknownError
            }
        } finally {
            connection.disconnect()
        }
    } catch (e: IOException) {
        return@withContext PwnedResult.NetworkError
    } catch (e: Exception) {
        return@withContext PwnedResult.UnknownError
    }
}