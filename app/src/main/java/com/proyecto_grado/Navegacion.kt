package com.proyecto_grado.Navegacion.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto_grado.Registar.kt.Registrar
import androidx.compose.material3.Text
import com.proyecto_grado.Recuperarcontraseña.kt.RecuperarContrasena
import com.proyecto_grado.login.kt.LoginScreen as Login


@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("Registrar") },
                onForgotPassword = { navController.navigate("recuperarcontrasena") } // ✔ Ya no es "luego lo hacemos"
            )
        }


        composable("Registrar"){
            Registrar(
                onBackToLogin = { navController.navigate("login") }
            )
        }

        composable("recuperarcontrasena") {
            RecuperarContrasena()
        }


        composable("main") {
            MainScreen()
        }
    }
}

@Composable
fun RecuperarContrasenaScreen(onNavigateToValidarCodigo: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun Recuperarcontrasena(onEnviarCodigo: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun Registrar(onBackToLogin: () -> Unit) {
        TODO("Not yet implemented")
}

@Composable
fun MainScreen() {
    Text("Bienvenido a NutriBovino")
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Login(
        onLoginSuccess = onLoginSuccess,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPassword = onForgotPassword,
        onNavigateToRecuperarcontrasena = {} // Puedes poner lógica después si la necesitas
    )
}

