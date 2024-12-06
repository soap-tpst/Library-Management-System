package com.soap.libms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    shape: Shape = RectangleShape,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPopUpWindow by remember { mutableStateOf(false) }

    var host by remember { mutableStateOf(Host.host) }
    var port by remember { mutableStateOf(Host.port) }
    var statusCheck by remember { mutableStateOf(false) }

    LaunchedEffect(statusCheck) {
        if (statusCheck) {
            while (true) {
                if (CurrentUserInstance.showDialog) {
                    showPopUpWindow = true
                    CurrentUserInstance.showDialog = false
                    break
                    statusCheck = false
                }
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxSize(),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    if (it.matches(Regex("^[a-zA-Z0-9]*$"))) username = it
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (it.matches(Regex("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]*$"))) password = it
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = host,
                    onValueChange = {
                        if (it.matches(Regex("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]*$"))) {
                            Host.host = it
                            host = it
                        }
                                    },
                    label = { Text("Host") },
                    modifier = Modifier.weight(2f).fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.width(16.dp))
                OutlinedTextField(
                    value = port.toString(),
                    onValueChange = {
                        if (it.matches(Regex("^\\d{1,5}$"))) {
                            Host.port = it.toInt()
                            port = it.toInt()
                        }
                                    },
                    label = { Text("Port") },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    singleLine = true
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val buttonModifier = if (windowSizeClass == WindowSizeClass.COMPACT) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                }
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            CurrentUserInstance.login(username, password)
                            statusCheck = true
                        }
                    }
                ) {
                    Text("Login")
                }
                if (showPopUpWindow) {
                    if (CurrentUserInstance.isLoggedIn) {
                        AlertDialog(
                            onDismissRequest = { showPopUpWindow = false },
                            title = { Text("Success") },
                            text = { Text("You have successfully logged in!") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showPopUpWindow = false
                                    onLoginSuccess()
                                }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                    else {
                        AlertDialog(
                            onDismissRequest = { showPopUpWindow = false },
                            title = { Text("Error") },
                            text = { Text("Invalid username or password") },
                            confirmButton = {
                                TextButton(onClick = { showPopUpWindow = false }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
