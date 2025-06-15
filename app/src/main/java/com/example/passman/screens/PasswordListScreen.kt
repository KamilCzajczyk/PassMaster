package com.example.passman.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.passman.R
import com.example.passman.SecurityLogin
import com.example.passman.SecuritySetup
import com.example.passman.data.AppDatabase
import com.example.passman.data.PasswordEntry
import com.example.passman.exportPasswordsToCSV
import com.example.passman.navigation.Screen
import com.example.passman.security.EncryptionUtils
import com.example.passman.ui.theme.Amaranth
import com.example.passman.ui.theme.BlueGreen
import com.example.passman.ui.theme.CandyAppleRed
import com.example.passman.ui.theme.CarlsbergGreen
import com.example.passman.ui.theme.FederalBlue
import com.example.passman.ui.theme.HonoluluBlue
import com.example.passman.ui.theme.MarianBlue
import com.example.passman.ui.theme.NonPhotoBlue
import com.example.passman.ui.theme.Persimon
import kotlinx.coroutines.launch

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PasswordListPreview() {
//    PasswordListScreen(navController = NavController(LocalContext.current))
//}


@Composable
fun PasswordListScreen(modifier: Modifier = Modifier, navController: NavController) {
//    val passwords = listOf(
//        StoredPassword(1, "Facebook", "www.facebook.com", "user123", "password123", "some comment"),
//        StoredPassword(
//            2,
//            "Facebook",
//            "www.facebook.com",
//            "user123",
//            "password1233",
//            "some comment",
//            fav = true
//        ),
//        StoredPassword(
//            3,
//            "Facebook",
//            "www.facebook.com",
//            "user123",
//            "password123",
//            "some comment",
//            fav = true
//        ),
//
//
//        )
    val db = AppDatabase.getDatabase(context = LocalContext.current)
    val dao = db.passwordEntryDao()

    val passwordsFlow = dao.getAllEntries()
    val passwords by passwordsFlow.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val mek = SecuritySetup.SessionManager.getMEK() ?: return
    val decryptedPasswords = passwords.map {
        val decryptedPassword = EncryptionUtils.decrypt(it.password, mek)
        it.copy(password = decryptedPassword)
    }

    PasswordList(modifier, decryptedPasswords, navController = navController)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordList(
    modifier: Modifier = Modifier,
    passwords: List<PasswordEntry>,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportdialog by remember { mutableStateOf(false) }
    var exportPath by remember { mutableStateOf<String?>(null) }

    var fabVisible by remember { mutableStateOf(true) }
    var lastIndex by remember { mutableStateOf(0) }
    var lastOffset by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val index = listState.firstVisibleItemIndex
        val offset = listState.firstVisibleItemScrollOffset
        fabVisible = if (index > lastIndex || (index == lastIndex && offset > lastOffset)) {
            // Scrolling down
            false
        } else {
            // Scrolling up
            true
        }
        lastIndex = index
        lastOffset = offset
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.your_passwords), color = Color.White) },
                actions = {
                    IconButton(
                        onClick = {
                            showExportdialog = true
//                            scope.launch {
//                                val path = exportPasswordsToCSV(context)
//                                if (path != null) {
//                                    Toast.makeText(context, "Exported to: $path", Toast.LENGTH_LONG).show()
//                                } else {
//                                    Toast.makeText(context, "Export failed", Toast.LENGTH_LONG).show()
//                                }
//                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_download_24),
                            contentDescription = stringResource(R.string.export),
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        SecurityLogin(context).logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = stringResource(R.string.logout),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FederalBlue,
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = FederalBlue
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(Screen.PasswordList.route) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_format_list_bulleted_24),
                            contentDescription = stringResource(R.string.home)
                        )
                    },
                    label = { Text(stringResource(R.string.passwords), color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.DataBreach.route) },
                    icon = {
                        Icon(
                            painterResource(R.drawable.outline_cloud_lock_24),
                            contentDescription = stringResource(R.string.data_breach)
                        )
                    },
                    label = { Text(stringResource(R.string.data_breach),color= Color.White) }
                )
            }
        },
        snackbarHost = {},
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MyExtendedFloatingActionButton(navController = navController)
            }

        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MarianBlue,
        contentColor = Color.Black,

        ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),

        ) {
            items(passwords) { passwords ->
                Spacer(modifier = Modifier.padding(2.dp))
                PasswordItem2(password = passwords, navController)
                Spacer(modifier = Modifier.padding(5.dp))

            }
        }

        if (showExportdialog) {
            val previewPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/passwords_export.csv"
            AlertDialog(
                onDismissRequest = { showExportdialog = false },
                title = { Text("Export Passwords") },
                text = { Text("Export passwords to:\n$previewPath") },
                confirmButton = {
                    TextButton(onClick = {
                        showExportdialog = false
                        scope.launch {
                            val path = exportPasswordsToCSV(context)
                            exportPath = path
                            Toast.makeText(
                                context,
                                if (path != null) "Exported to: $path" else "Export failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showExportdialog = false }) { Text("Cancel") }
                }
            )
        }
    }

}

@Composable
fun MyExtendedFloatingActionButton(navController: NavController) {
    ExtendedFloatingActionButton(
        onClick = {
            navController.navigate(Screen.AddPassword.route)
        },
        modifier = Modifier.padding(10.dp),
        icon = { Icon(Icons.Default.AddCircle, stringResource(R.string.add)) },
        text = { Text(stringResource(R.string.add)) },
        containerColor = HonoluluBlue,
        contentColor = Color.White,
    )
}

@Composable
fun PasswordItem2(password: PasswordEntry, navController: NavController) {
    var expandedState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context = context)
    val dao = db.passwordEntryDao()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.delete_password)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_password)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.deleteEntryById(password.id)
                        showDialog = false
                    }
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = NonPhotoBlue,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(password.id) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    },
                    onTap = {
                        expandedState = !expandedState
                    }
                )
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End // Align items to the end (right)
            ) {
                IconButton(onClick = {
                    scope.launch {
                        val entry = dao.getAll().find { it.id == password.id }
                        if(entry != null){
                            val updatedEntry = entry.copy(isFavorite = !entry.isFavorite)
                            dao.update(updatedEntry)
                        }
                        //dao.update(password.copy(isFavorite = !password.isFavorite))
                    }
                }) {
                    Icon(
                        painter = if (password.isFavorite) {
                            painterResource(R.drawable.baseline_favorite_24)
                        } else {
                            painterResource(R.drawable.baseline_favorite_border_24)
                        },
                        contentDescription = stringResource(R.string.favourite),
                        tint = if (password.isFavorite) Persimon else Color.Black
                    )
                }
                Text(
                    text = password.serviceName,
                    modifier = Modifier
                        .weight(1f) // Makes the Text take up all available space
                        .padding(
                            start = 2.dp,
                            end = 2.dp,
                            bottom = 2.dp,
                            top = 2.dp
                        ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )

                IconButton(
                    onClick = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip =
                            ClipData.newPlainText("password", password.password)
                        clipboard.setPrimaryClip(clip)
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_content_copy_24),
                        contentDescription = stringResource(R.string.copy)
                    )
                }

                IconButton(
                    onClick = { expandedState = !expandedState }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.expand),
                        modifier = Modifier.rotate(if (expandedState) 0f else 90f)
                    )
                }
            }
            if (expandedState) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    verticalAlignment = Alignment.Bottom // Wyrównaj elementy na dole
                ) {
                    Column(
                        modifier = Modifier.weight(1f) // Tekst zajmuje całą dostępną przestrzeń
                    ) {
                        Text("Login: " + password.login)
                        Text("Password: " + password.password)
                    }

                    TextButton(
                        onClick = {
                            navController.navigate(Screen.EditPassword.createRoute(password.id))
                        },
                        modifier = Modifier.padding(4.dp), // Przyciski powinny mieć padding
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Persimon
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(stringResource(R.string.edit), color = Color.White)
                    }
                }
            }
        }
    }
}