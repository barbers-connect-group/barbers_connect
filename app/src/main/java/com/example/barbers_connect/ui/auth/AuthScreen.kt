package com.barbersconnect.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { AuthBottomNavigation(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AuthNavHost(navController)
        }
    }
}

@Composable
fun AuthNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen() }
        composable("register") { RegisterScreen() }
    }
}

@Composable
fun AuthBottomNavigation(navController: NavHostController) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        NavigationBarItem(
            selected = currentRoute == "login",
            onClick = { navController.navigate("login") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Login") },
            label = { Text("Login") }
        )

        NavigationBarItem(
            selected = currentRoute == "register",
            onClick = { navController.navigate("register") },
            icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Cadastro") },
            label = { Text("Cadastro") }
        )
    }
}

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Senha") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {  }) {
            Text("Entrar")
        }
    }
}

@Composable
fun RegisterScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Cadastro", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nome") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Senha") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Cadastrar")
        }
    }
}

