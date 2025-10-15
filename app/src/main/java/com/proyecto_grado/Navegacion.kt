package com.proyecto_grado.Navegacion.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto_grado.Registar.kt.Registrar
import com.proyecto_grado.RecuperarContrasena
import com.proyecto_grado.login.kt.LoginScreen
import com.proyecto_grado.Menu.kt.MenuScreen
import com.proyecto_grado.RegistarAlimento.RegistrarAlimentoScreen
import com.proyecto_grado.RegistroAnimal.RegistrarAnimalScreen
import com.proyecto_grado.RegistroLote.kt.RegistrarLoteScreen
import com.proyecto_grado.RegistarAlimento.ListaAlimentoScreen
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.proyecto_grado.Alimentacion.AlimentacionScreen
import com.proyecto_grado.Potrero.PotreroScreen
import com.proyecto_grado.Reporte.ReporteScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.proyecto_grado.DetalleReporte.DetalleReporteScreen
import com.proyecto_grado.DetalleReporte.ReporteAnimal
import com.google.gson.Gson
import com.proyecto_grado.Perfil.PerfilUsuarioScreen




@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val listaAlimentos = remember { mutableStateListOf("Pasto kikuyo", "Melaza") }



    NavHost(navController = navController, startDestination = "login") {

        // 🔹 LOGIN
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("Registrar") },
                onForgotPassword = { navController.navigate("recuperarcontrasena") },
                onNavigateToRecuperarcontrasena = { navController.navigate("recuperarcontrasena") }
            )
        }



        // 🔹 REGISTRAR USUARIO
        composable("Registrar") {
            Registrar(onBackToLogin = { navController.navigate("login") })
        }

        // 🔹 RECUPERAR CONTRASEÑA
        composable("recuperarcontrasena") { RecuperarContrasena() }

        // 🔹 MENÚ PRINCIPAL
        composable("main") {
            MenuScreen(
                onRegistrarAnimal = { navController.navigate("registrarAnimal") },
                onRegistrarLote = { navController.navigate("registrarLote") },
                onRegistrarAlimento = { navController.navigate("registrarAlimento") },
                onAlimentacion = { navController.navigate("alimentacion") },
                onPotrero = { navController.navigate("potrero") },
                onReportes = { navController.navigate("reportes") },
                onPerfilUsuario = { navController.navigate("perfilUsuario") } // ✅ aquí
            )
        }

        // 🔹 REGISTRAR ANIMAL
        composable("registrarAnimal") {
            RegistrarAnimalScreen(onBack = { navController.popBackStack() })
        }

        // 🔹 REGISTRAR LOTE
        composable("registrarLote") {
            RegistrarLoteScreen(
                onBack = { navController.popBackStack() },
                onLoteGuardado = { numero, observacion ->
                    println("Lote guardado: $numero - $observacion")
                }
            )
        }


        // 🔹 REGISTRAR ALIMENTO
        composable("registrarAlimento") {
            RegistrarAlimentoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 🔹 ALIMENTACIÓN
        composable("alimentacion") {
            AlimentacionScreen(
                onBack = { navController.popBackStack() },
                animales = emptyList(),
                alimentos = emptyList(),
                lotes = emptyList(),
            )
        }




        // 🔹 POTRERO
        composable("potrero") {
            PotreroScreen(onBack = { navController.popBackStack() })
        }

        // 🔹 REPORTES
        composable("reportes") {
            ReporteScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // 🔹 DETALLE DE REPORTE
        composable(
            "detalleReporte/{reporteJson}",
            arguments = listOf(navArgument("reporteJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val gson = Gson()
            val reporteJson = backStackEntry.arguments?.getString("reporteJson")
            val reporte = gson.fromJson(reporteJson, ReporteAnimal::class.java)

            DetalleReporteScreen(
                onBack = { navController.popBackStack() },
                reporte = reporte
            )
        }

        // 🔹 PERFIL DE USUARIO
        composable("perfilUsuario") {
            PerfilUsuarioScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true } // ✅ corregido
                    }
                }
            )
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    TODO("Not yet implemented")
}





