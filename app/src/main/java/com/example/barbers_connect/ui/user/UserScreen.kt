package com.example.barbers_connect.ui.user

import UserService
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.barbers_connect.formatIsoDate
import com.example.barbers_connect.getColorScheme
import com.example.barbers_connect.model.BarberShop
import com.example.barbers_connect.model.User
import com.example.barbers_connect.service.BarberShopService
import com.example.barbers_connect.service.WelcomeMesageService

var userLogged = false


@Composable
fun UserScreen(navController: NavHostController, onNavigateToLogin: () -> Unit,
               onNavigateToRegister: () -> Unit, onNavigateToProfile: () -> Unit,
               onNavigateToBarbershops: () -> Unit, onNavigateToUserProfile: () -> Unit) {
    val context = LocalContext.current
    val startDestination = if (!userLogged) "login" else "perfil"

    MaterialTheme(
        colorScheme = getColorScheme()
    ) {
        Scaffold(
            bottomBar = {
                if (!userLogged) {
                    AuthBottomNavigationBar(onNavigateToLogin, onNavigateToRegister, onNavigateToProfile,
                        onNavigateToBarbershops, onNavigateToUserProfile)
                } else {
                    MainBottomNavigationBar(onNavigateToBarbershops, onNavigateToUserProfile)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") { LoginScreen(onNavigateToRegister, onNavigateToBarbershops, onNavigateToUserProfile) }
                composable("register") { RegisterScreen(onNavigateToLogin) }
                composable("profile") {ProfileScreen(onNavigateToProfile)}
                //composable("barbershops") {BarbershopsScreen()}
                composable("userprofile") {UserProfileScreen()}
            }
        }
    }
}

@Composable
fun AuthBottomNavigationBar(onNavigateToLogin: () -> Unit, onNavigateToRegister: () -> Unit,
                            onNavigateToProfile: () -> Unit, onNavigateToBarbershops: () -> Unit,
                            onNavigateToUserProfile: () -> Unit) {
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
fun MainBottomNavigationBar(onNavigateToBarbershops: () -> Unit, onNavigateToUserProfile: () -> Unit) {
    val selectedColor = Color(0xFF4CAF50)
    val defaultColor = Color.White
    val backgroundColor = Color(0xFF795548)

    NavigationBar(containerColor = backgroundColor, tonalElevation = 8.dp) {
        val items = listOf("Barbearias" to Icons.Default.ContentCut, "Perfil" to Icons.Default.ContactMail)
        val actions = listOf(onNavigateToBarbershops, onNavigateToUserProfile)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToBarbershops: () -> Unit,
                onNavigateToUserProfile: () -> Unit) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = Color(0xFFF5F5DC),
        focusedBorderColor = Color(0xFF795548),
        unfocusedBorderColor = Color(0xFFBCAAA4)
    )
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null)  }
    val context = LocalContext.current
    var welcomeMessage by remember { mutableStateOf<String?>(null) }
    val welcomeMesageService = WelcomeMesageService()

    LaunchedEffect(Unit) {
        welcomeMesageService.search { result ->
            welcomeMessage = result?.message
        }
    }

    fun login() {
        val user = User(username = username, password = password)

        UserService.login(context, user) { result ->
            val status = result["status"].toString()
            message = result["message"].toString()

            if (status == "success") {
                userLogged = true
                onNavigateToUserProfile()
            //onNavigateToBarbershops()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(56.dp))
        welcomeMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF795548),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    onValueChange = { username = it },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    label = { Text("Senha", color = Color(0xFF795548)) },
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { login() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                    modifier = Modifier.fillMaxWidth(0.80F).padding(top = 16.dp),
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
    }

}

@Composable
fun UserProfileScreen() {
    var user by remember { mutableStateOf<User?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        UserService.current_user(context) { result ->
            val status = result["status"].toString()
            message = result["message"].toString()

            if (status == "success") {
                user = result["data"] as User
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fundo cinza claro
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Icon",
                    tint = Color(0xFF795548),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Perfil do Usuário",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )
                Spacer(modifier = Modifier.height(16.dp))

                user?.let {
                    UserProfileItem(label = "Nome de usuário", value = it.username)
                    UserProfileItem(label = "Email", value = it.email.toString())
                    UserProfileItem(label = "Conta criada em", value = formatIsoDate(it.createdAt.toString()))
                } ?: CircularProgressIndicator(color = Color(0xFF795548))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
            modifier = Modifier
                .fillMaxWidth(0.75F)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Sair",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
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
fun UserProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateToEditProfile: () -> Unit) {
    var barbershop by remember { mutableStateOf<BarberShop?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    BarberShopService.getBarberShopProfile(context = context, barberShopId = 150) { profile, error ->
        if (profile != null) {
            barbershop = profile
        } else {
            errorMessage = error
        }
    }

    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color.Red)
    }

    barbershop?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    },
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF795548)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Endereço", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF795548))
                                    Text(text = it.address, color = Color.Black)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Telefone", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF795548))
                                    Text(text = it.phone, color = Color.Black)
                                }
                            }
                            Text(text = "Descrição", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF795548))
                            Text(text = it.description, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = it.startShift,
                            onValueChange = {},
                            label = { Text("Abertura", color = Color(0xFF795548)) },
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = it.endShift,
                            onValueChange = {},
                            label = { Text("Fechamento", color = Color(0xFF795548)) },
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Cortes Disponíveis",
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
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = Color(0xFFF5F5DC),
        focusedBorderColor = Color(0xFF795548),
        unfocusedBorderColor = Color(0xFFBCAAA4)
    )
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    fun register() {
        val user = User(username = username, email = email, password = password)

        UserService.register(user) { result ->
            val status = result["status"].toString()
            message = result["message"].toString()

            if (status == "success") {
                onNavigateToLogin()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cadastro",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF795548) // Marrom
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    label = { Text("Username", color = Color(0xFF795548)) },
                    value = username,
                    onValueChange = { username = it },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    label = { Text("E-mail", color = Color(0xFF795548)) },
                    value = email,
                    onValueChange = { email = it },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    label = { Text("Senha", color = Color(0xFF795548)) },
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { register() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Cadastrar",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Já tem uma conta? Faça login",
                        color = Color(0xFF795548),
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }

        LaunchedEffect(message) {
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                message = null
            }
        }
    }
}