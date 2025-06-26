package com.example.test_cooking.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.test_cooking.data.Dish
import com.example.test_cooking.network.DishService
import com.example.test_cooking.data.AuthManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDishScreen(
    onDishAdded: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dishService = remember { DishService(context) }
    val authManager = remember { AuthManager(context) }

    var selectedImageUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }

    val listStringSaver: Saver<SnapshotStateList<String>, Any> = listSaver(
        save = { list -> ArrayList(list) },
        restore = { list -> list.toMutableStateList() }
    )

    var currentIngredient by rememberSaveable { mutableStateOf("") }
    val ingredientsList = rememberSaveable(saver = listStringSaver) { mutableStateListOf<String>() }

    var recipe by rememberSaveable { mutableStateOf("") }

    var showImageError by remember { mutableStateOf(false) }
    var showTitleError by remember { mutableStateOf(false) }
    var showDescriptionError by remember { mutableStateOf(false) }
    var showIngredientsListError by remember { mutableStateOf(false) }
    var showRecipeError by remember { mutableStateOf(false) }
    var showCategoryError by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showApiError by remember { mutableStateOf(false) }
    var apiErrorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            showImageError = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить новое блюдо") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Изображение блюда *", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Изображение блюда",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .then(if (showImageError) Modifier.background(Color.Red.copy(alpha = 0.1f)) else Modifier),
                    contentScale = ContentScale.Crop,
                )
                if (showImageError) {
                    Text(
                        text = "Пожалуйста, выберите изображение",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Выбрать из галереи")
                    Spacer(Modifier.width(8.dp))
                    Text("Выбрать из галереи")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    showTitleError = it.isBlank()
                },
                label = { Text("Название рецепта *") },
                isError = showTitleError,
                supportingText = { if (showTitleError) Text("Поле обязательно") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    showDescriptionError = it.isBlank()
                },
                label = { Text("Описание *") },
                isError = showDescriptionError,
                supportingText = { if (showDescriptionError) Text("Поле обязательно") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                    showCategoryError = it.isBlank()
                },
                label = { Text("Категория *") },
                isError = showCategoryError,
                supportingText = { if (showCategoryError) Text("Поле обязательно") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Ингредиенты *", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentIngredient,
                        onValueChange = { currentIngredient = it },
                        label = { Text("Добавить ингредиент") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (currentIngredient.isNotBlank()) {
                                ingredientsList.add(currentIngredient.trim())
                                currentIngredient = ""
                                showIngredientsListError = false
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить ингредиент")
                    }
                }
                if (ingredientsList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ingredientsList.forEachIndexed { index, ingredient ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "• $ingredient", modifier = Modifier.weight(1f))
                            IconButton(onClick = { ingredientsList.removeAt(index) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Удалить ингредиент")
                            }
                        }
                    }
                }
                if (showIngredientsListError) {
                    Text(text = "Добавьте хотя бы один ингредиент", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = recipe,
                onValueChange = {
                    recipe = it
                    showRecipeError = it.isBlank()
                },
                label = { Text("Рецепт *") },
                isError = showRecipeError,
                supportingText = { if (showRecipeError) Text("Поле обязательно") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
            }

            if (showApiError) {
                Text(
                    text = apiErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    showImageError = (selectedImageUri == null)
                    showTitleError = title.isBlank()
                    showDescriptionError = description.isBlank()
                    showCategoryError = category.isBlank()
                    showIngredientsListError = ingredientsList.isEmpty()
                    showRecipeError = recipe.isBlank()
                    showApiError = false

                    if (!showImageError && !showTitleError && !showDescriptionError && !showIngredientsListError && !showRecipeError && !showCategoryError) {
                        val authToken = authManager.getAuthToken()
                        if (authToken == null) {
                            apiErrorMessage = "Ошибка: Пользователь не авторизован."
                            showApiError = true
                            return@Button
                        }

                        val newDishForUpload = Dish(
                            title = title,
                            description = description,
                            ingredients = ingredientsList,
                            recipe = recipe,
                            category = category,
                            photo = selectedImageUri!!.toString(),
                        )

                        isLoading = true
                        coroutineScope.launch {
                            val success = dishService.createDish(
                                dish = newDishForUpload,
                                imageUri = selectedImageUri!!,
                                authToken = authToken
                            )
                            isLoading = false

                            if (success) {
                                onDishAdded()
                            } else {
                                apiErrorMessage = "Ошибка создания блюда. Пожалуйста, попробуйте еще раз."
                                showApiError = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Добавить блюдо")
            }
        }
    }
}