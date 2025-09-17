package com.proyecto_grado.Navegacion.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto_grado.Registar.kt.Registrar
import androidx.compose.material3.Text
import com.proyecto_grado.RecuperarContrasena
import com.proyecto_grado.login.kt.LoginScreen
import com.proyecto_grado.RecuperarContrasena
import com.proyecto_grado.Menu.kt.MenuScreen
import com.proyecto_grado.RegistroAnimal.RegistrarAnimalScreen


@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true } // evita volver al login con back
                    }
                },
                onNavigateToRegister = { navController.navigate("Registrar") },
                onForgotPassword = { navController.navigate("recuperarcontrasena") },
                onNavigateToRecuperarcontrasena = { navController.navigate("recuperarcontrasena") }
            )
        }

        composable("Registrar") {
            Registrar(
                onBackToLogin = { navController.navigate("login") }
            )
        }

        composable("recuperarcontrasena") {
            RecuperarContrasena()
        }

        composable("main") {
            MenuScreen(
                onRegistrarAnimal = { navController.navigate("registrarAnimal") },
                onRegistrarLote = { navController.navigate("registrarLote") },
                onRegistrarAlimento = { navController.navigate("registrarAlimento") },
                onAlimentacion = { navController.navigate("alimentacion") },
                onPotrero = { navController.navigate("potrero") },
                onReportes = { navController.navigate("reportes") }
            )
        }

        // Aquí puedes ir agregando las pantallas nuevas
        composable("registrarAnimal") {
            RegistrarAnimalScreen(
                onBack = { navController.popBackStack() }) }
        composable("registrarLote") { /* Pantalla Registrar Lote */ }
        composable("registrarAlimento") { /* Pantalla Registrar Alimento */ }
        composable("alimentacion") { /* Pantalla Alimentación */ }
        composable("potrero") { /* Pantalla Potrero */ }
        composable("reportes") { /* Pantalla Reportes */ }
    }
}


