package com.example.barbers_connect.ui.user

import UserService
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.barbers_connect.model.BarberShop
import com.example.barbers_connect.model.User
import com.example.barbers_connect.service.BarberShopService

@Composable
fun UserScreen(navController: NavHostController, onNavigateToLogin: () -> Unit,
               onNavigateToRegister: () -> Unit, onNavigateToProfile: () -> Unit,
               onNavigateToBarbershops: () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme(
            primary = Color(0xFF795548),
            onPrimary = Color.White,
            primaryContainer = Color.White,
            onPrimaryContainer = Color.White,
            inversePrimary = Color.White,
            secondary = Color(0xFF5D4037),
            onSecondary = Color.White,
            secondaryContainer = Color.White,
            onSecondaryContainer = Color.White,
            tertiary = Color(0xFF4E342E),
            onTertiary = Color.White,
            tertiaryContainer = Color.White,
            onTertiaryContainer = Color.White,
            background = Color(0xFFF5F5DC),
            onBackground = Color.Black,
            surface = Color(0xFFF5F5DC),
            onSurface = Color.Black,
            surfaceVariant = Color.White,
            onSurfaceVariant = Color.White,
            surfaceTint = Color.White,
            inverseSurface = Color.White,
            inverseOnSurface = Color.White,
            error = Color.Red,
            onError = Color.White,
            errorContainer = Color.White,
            onErrorContainer = Color.White,
            outline = Color.White,
            outlineVariant = Color.White,
            scrim = Color.White,
            surfaceBright = Color.White,
            surfaceDim = Color.White,
            surfaceContainer = Color.White,
            surfaceContainerHigh = Color.White,
            surfaceContainerHighest = Color.White,
            surfaceContainerLow = Color.White,
            surfaceContainerLowest = Color.White,
        )
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    onNavigateToLogin, onNavigateToRegister, onNavigateToProfile,
                    onNavigateToBarbershops
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") { LoginScreen(onNavigateToRegister, onNavigateToBarbershops) }
                composable("register") { RegisterScreen(onNavigateToLogin) }
                composable("profile") {ProfileScreen(onNavigateToProfile)}
                //composable("barbershops") {BarbershopsScreen()}
            }
        }
    }
}

@Composable
fun BottomNavigationBar(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit,
                        onNavigateToProfile: () -> Unit, onNavigateToBarbershops: () -> Unit) {
    val selectedColor = Color(0xFF4CAF50)
    val defaultColor = Color.White
    val backgroundColor = Color(0xFF795548)

    NavigationBar(containerColor = backgroundColor, tonalElevation = 8.dp) {
        val items = listOf("Login" to Icons.Default.Person, "Cadastro" to Icons.Default.PersonAdd,"Perfil" to Icons.Default.Person)
        val actions = listOf(onNavigateToLogin, onNavigateToRegister, onNavigateToProfile)
        var selectedItem by remember { mutableStateOf(0) }

        items.forEachIndexed { index, (label, icon) ->
            NavigationBarItem(
                label = {
                    Text(
                        label,
                        color = if (selectedItem == index) selectedColor else defaultColor
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    actions[index]()
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedItem == index) selectedColor else defaultColor
                    )
                }
            )
        }
    }
}

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToBarbershops: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null)  }
    val context = LocalContext.current

    fun login() {
        val user = User(username, password)

        UserService.login(context, user) { result ->
            val status = result["status"].toString()
            message = result["message"].toString()

            if (status == "success") {
                onNavigateToBarbershops()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF795548) // Marrom
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            label = { Text("Username", color = Color(0xFF795548)) },
            value = username,
            onValueChange = { username = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            label = { Text("Senha", color = Color(0xFF795548)) },
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { login() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
            modifier = Modifier.fillMaxWidth(0.75F).padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Entrar",
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton (onClick = onNavigateToRegister) {
            Text(
                text = "Esqueci minha senha",
                color = Color(0xFF795548),
                textDecoration = TextDecoration.Underline
            )
        }
        LaunchedEffect(message) {
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                message = null
            }
        }
    }
}

@Composable
fun ProfileScreen(onNavigateToEditProfile: () -> Unit) {
    // Fetch the profile data
    var barbershop by remember { mutableStateOf<BarberShop?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    BarberShopService.getBarberShopProfile(context = context, barberShopId = 140) {profile, error ->
        if (profile != null) {
            barbershop = profile
        } else {
            errorMessage = error
        }
    }

    // If error message exists, display it
    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color.Red)
    }

    // Display profile data once it is fetched
    barbershop?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make the content scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Barbershop Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF795548)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = it.name,
                onValueChange = {},
                label = { Text("Barbershop Name", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = it.description,
                onValueChange = {},
                label = { Text("Description", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = it.address,
                onValueChange = {},
                label = { Text("Address", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = it.phone,
                onValueChange = {},
                label = { Text("Phone", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = it.startShift,
                onValueChange = {},
                label = { Text("Start Shift", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = it.endShift,
                onValueChange = {},
                label = { Text("End Shift", color = Color(0xFF795548)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Available Cuts",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF795548)
            )
            Spacer(modifier = Modifier.height(8.dp))

            it.tags.forEach { cut ->
                Text(text = "- $cut", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToEditProfile,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                modifier = Modifier.fillMaxWidth(0.75F).padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Edit Profile", color = Color.White)
            }
        }
    }
}


@Composable
fun RegisterScreen(onRegisterClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Cadastro", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF795548))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username", color = Color(0xFF795548)) })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail", color = Color(0xFF795548)) })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha", color = Color(0xFF795548)) })
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
            modifier = Modifier.fillMaxWidth(0.75F).padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Cadastrar",
                color = Color.White
            )
        }
    }
}
