package com.example.test_cooking.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_cooking.data.RegistrationData
import com.example.test_cooking.network.AuthService
import kotlinx.coroutines.launch
import com.example.test_cooking.data.AuthManager

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLoginClick: () -> Unit,
) {
    val context = LocalContext.current

    var nickname by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    var nicknameError by remember { mutableStateOf<String?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var registerErrorMessage by remember { mutableStateOf<String?>(null) }

    val authService = remember { AuthService(context) }
    val authManager = remember { AuthManager(context) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Создайте аккаунт",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = {
                    nickname = it
                    nicknameError = if (it.isBlank()) "Никнейм не может быть пустым" else null
                    registerErrorMessage = null
                },
                label = { Text("Никнейм") },
                leadingIcon = { Icon(Icons.Default.Face, contentDescription = "Никнейм") },
                singleLine = true,
                isError = nicknameError != null,
                supportingText = {
                    if (nicknameError != null) {
                        Text(
                            text = nicknameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                    loginError = if (it.isBlank()) "Логин не может быть пустым" else null
                    registerErrorMessage = null
                },
                label = { Text("Логин") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Логин") },
                singleLine = true,
                isError = loginError != null,
                supportingText = {
                    if (loginError != null) {
                        Text(
                            text = loginError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(it)
                    confirmPasswordError =
                        if (confirmPassword.isNotBlank() && it != confirmPassword) "Пароли не совпадают" else null
                    registerErrorMessage = null
                },
                label = { Text("Пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Пароль") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = passwordError != null,
                trailingIcon = {
                    val image =
                        if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisibility) "Скрыть пароль" else "Показать пароль"
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                supportingText = {
                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = if (it != password) "Пароли не совпадают" else null
                    registerErrorMessage = null
                },
                label = { Text("Повторите пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Повторите пароль") },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = confirmPasswordError != null,
                trailingIcon = {
                    val image =
                        if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (confirmPasswordVisibility) "Скрыть пароль" else "Показать пароль"
                    IconButton(onClick = {
                        confirmPasswordVisibility = !confirmPasswordVisibility
                    }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(
                            text = confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (registerErrorMessage != null) {
                Text(
                    text = registerErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    nicknameError = if (nickname.isBlank()) "Никнейм не может быть пустым" else null
                    loginError = if (login.isBlank()) "Логин не может быть пустым" else null
                    passwordError = validatePassword(password)
                    confirmPasswordError =
                        if (password != confirmPassword) "Пароли не совпадают" else null

                    if (nicknameError == null && loginError == null && passwordError == null && confirmPasswordError == null) {
                        val registrationData = RegistrationData(
                            nickname = nickname,
                            login = login,
                            password = password,
                        )
                        coroutineScope.launch {
                            when (val result = authService.registerUser(registrationData)) {
                                is AuthService.AuthResult.Success -> {
                                    authManager.saveAuthToken(result.authResponse.token)
                                    onRegisterSuccess()
                                }
                                is AuthService.AuthResult.Error -> {
                                    registerErrorMessage = "Ошибка сервера"
                                }
                                is AuthService.AuthResult.NetworkError -> {
                                    registerErrorMessage = "Ошибка сети. Проверьте ваше интернет-соединение."
                                }
                            }

                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = nickname.isNotBlank() && login.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() &&
                        nicknameError == null && loginError == null && passwordError == null && confirmPasswordError == null
            ) {
                Text("Зарегистрироваться", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLoginClick) {
                Text(
                    text = "Уже есть аккаунт? Войти!",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun validatePassword(password: String): String? {
    if (password.length < 8) {
        return "Пароль должен быть минимум 8 символов"
    }
    if (!password.contains(Regex("[a-zA-Z]"))) {
        return "Пароль должен содержать буквы"
    }
    if (!password.contains(Regex("[0-9]"))) {
        return "Пароль должен содержать цифры"
    }
    return null
}