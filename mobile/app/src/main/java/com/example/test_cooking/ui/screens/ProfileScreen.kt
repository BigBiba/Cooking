package com.example.test_cooking.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test_cooking.data.AnswerDish
import com.example.test_cooking.data.AuthManager
import com.example.test_cooking.data.User
import com.example.test_cooking.network.AuthService
import com.example.test_cooking.network.DishService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onDishClick: (AnswerDish) -> Unit
) {
    val context = LocalContext.current
    val authService = remember { AuthService(context) }
    val dishService = remember { DishService(context) }
    val authManager = remember { AuthManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var userProfile by remember { mutableStateOf<User?>(null) }
    var userDishes by remember { mutableStateOf<List<AnswerDish>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {

            when (val userResult = authService.getMyProfile()) {
                is AuthService.UserProfileResult.Success -> {
                    userProfile = userResult.user
                }
                is AuthService.UserProfileResult.Error -> {
                    errorMessage = userResult.message
                }
                is AuthService.UserProfileResult.NetworkError -> {
                    errorMessage = "Ошибка сети при загрузке профиля. Проверьте подключение."
                }
            }

            when (val dishesResult = dishService.getMyDishes(authManager.getAuthToken()!!)) {
                is DishService.DishesResult.Success -> {
                    userDishes = dishesResult.dishes
                }
                is DishService.DishesResult.Error -> {
                    val dishErrorMessage = dishesResult.message
                    errorMessage = if (errorMessage == null) dishErrorMessage else "Ошибка"
                }
                is DishService.DishesResult.NetworkError -> {
                    val networkErrorMessage = "Ошибка сети при загрузке блюд. Проверьте подключение."
                    errorMessage = if (errorMessage == null) networkErrorMessage else "Ошибка"
                }
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF261f05),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Иконка пользователя",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    userProfile?.let { user ->
                        Column {
                            Text(
                                text = user.nickname,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } ?: run {
                        Text(
                            text = "Имя пользователя не загружено",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Мои рецепты",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (userDishes.isEmpty()) {
                    Text(
                        text = "Вы пока не создали ни одного рецепта.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(userDishes) { dish ->
                            DishCard(dish = dish) {
                                onDishClick(dish)
                            }
                        }
                    }
                }
            }
        }
    }
}


