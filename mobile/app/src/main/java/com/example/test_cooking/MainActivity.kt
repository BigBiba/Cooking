@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.test_cooking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test_cooking.data.AuthManager
import com.example.test_cooking.data.AnswerDish
import com.example.test_cooking.ui.screens.AddDishScreen
import com.example.test_cooking.ui.screens.DishesScreen
import com.example.test_cooking.ui.screens.LoginScreen
import com.example.test_cooking.ui.screens.RegisterScreen
import com.example.test_cooking.ui.theme.Test_cookingTheme
import com.google.gson.Gson
import com.example.test_cooking.network.AuthService
import com.example.test_cooking.network.DishService
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.test_cooking.ui.screens.DishDetailScreen
import com.example.test_cooking.ui.screens.ProfileScreen
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Test_cookingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val dishService = remember { DishService(context) }

    var isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }

    var dishes by remember { mutableStateOf<List<AnswerDish>>(emptyList()) }

    var isLoadingDishes by remember { mutableStateOf(true) }

    var dishesLoadError by remember { mutableStateOf<String?>(null) }


    val coroutineScope = rememberCoroutineScope()

    val loadDishes: () -> Unit = {
        isLoadingDishes = true
        dishesLoadError = null
        coroutineScope.launch {
            when (val result = dishService.getAllDishes()) {
                is DishService.DishesResult.Success -> {
                    dishes = result.dishes
                    isLoadingDishes = false
                }

                is DishService.DishesResult.Error -> {
                    dishesLoadError =
                        "Ошибка"
                    isLoadingDishes = false
                }

                is DishService.DishesResult.NetworkError -> {
                    dishesLoadError = "Ошибка сети: Проверьте ваше интернет-соединение."
                    isLoadingDishes = false
                }
            }
        }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == "dishes_list") {
                loadDishes()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cooking") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF261f05),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = {
                            navController.navigate("add_dish_screen")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить новое блюдо",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    IconButton(onClick = {
                        if (isLoggedIn) {
                            navController.navigate("profile_screen")
                        } else {
                            navController.navigate("login_screen")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Профиль пользователя",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dishes_list",
            modifier = Modifier.padding(start = paddingValues.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr))
                .fillMaxSize()
        ) {
            composable("dishes_list") { backStackEntry ->
                when {
                    isLoadingDishes -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    dishesLoadError != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dishesLoadError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }

                    else -> {
                        DishesScreen(
                            dishes = dishes,
                            onDishClick = { dish ->
                                val dishJson = Gson().toJson(dish)
                                val encodedDishJson =
                                    URLEncoder.encode(dishJson, StandardCharsets.UTF_8.toString()).replace("+", "%20")
                                navController.navigate("dish_detail_screen/${encodedDishJson}")
                            }
                        )
                    }
                }
            }

            composable(
                route = "dish_detail_screen/{dishJson}",
                arguments = listOf(navArgument("dishJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val dishJson = backStackEntry.arguments?.getString("dishJson")
                val dish = Gson().fromJson(
                    dishJson,
                    AnswerDish::class.java
                )
                if (dish != null) {
                    DishDetailScreen(
                        dish = dish,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    Text("Ошибка загрузки данных о блюде", modifier = Modifier.padding(16.dp))
                }
            }


            composable("login_screen") {
                LoginScreen(
                    onLoginClick = {
                        isLoggedIn = authManager.isLoggedIn()
                        navController.navigate("dishes_list") {
                            popUpTo("login_screen") { inclusive = true }
                        }
                        loadDishes()
                    },
                    onRegisterClick = {
                        navController.navigate("register_screen")
                    },
                    errorMessage = null
                )
            }

            composable("register_screen") {
                val authService = remember { AuthService(context) }
                RegisterScreen(
                    onRegisterSuccess = {
                        val token =
                            authService.currentAuthToken
                        if (token != null) {
                            authManager.saveAuthToken(token)
                            isLoggedIn = authManager.isLoggedIn()
                            navController.navigate("dishes_list") {
                                popUpTo("register_screen") { inclusive = true }
                            }
                        } else {
                            Log.e("AppNavigation", "Registration success but token is null!")
                        }
                    },
                    onBackToLoginClick = {
                        navController.navigate("login_screen") {
                            popUpTo("register_screen") { inclusive = true }
                        }
                    }
                )
            }

            composable("profile_screen") {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onDishClick = { dish ->
                        val dishJson = Gson().toJson(dish)
                        val encodedDishJson = URLEncoder.encode(dishJson, StandardCharsets.UTF_8.toString())
                            .replace("+", "%20")
                        navController.navigate("dish_detail_screen/${encodedDishJson}")
                    }
                )
            }

            composable("add_dish_screen") {
                AddDishScreen(
                    onDishAdded = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}