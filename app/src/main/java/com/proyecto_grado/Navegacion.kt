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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.proyecto_grado.Potrero.PotreroScreen
import com.proyecto_grado.Alimentacion.AlimentacionScreen
import com.proyecto_grado.AlimentacionRepository
import com.proyecto_grado.Perfil.PerfilUsuarioScreen
import com.proyecto_grado.Reporte.GenerarReporteScreen
import com.proyecto_grado.Reporte.ReporteGeneralScreen
import androidx.compose.ui.platform.LocalContext
import com.proyecto_grado.ListaAlimentoScreen
import com.proyecto_grado.RegistroLote.RegistrarLoteScreen
import com.proyecto_grado.ReporteGeneral


@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val listaAlimentos = remember { mutableStateListOf("Pasto kikuyo", "Melaza") }
    val context = LocalContext.current

    val repo = remember(context ){ AlimentacionRepository (context)}

    NavHost(navController = navController, startDestination = "login") {

        //  LOGIN
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



        //  REGISTRAR USUARIO
        composable("Registrar") {
            Registrar(onBackToLogin = { navController.navigate("login") })
        }

        //  RECUPERAR CONTRASEÑA
        composable("recuperarcontrasena") { RecuperarContrasena() }

        //  MENÚ PRINCIPAL
        composable("main") {
            MenuScreen(
                onRegistrarAnimal = { navController.navigate("registrarAnimal") },
                onRegistrarLote = { navController.navigate("registrarLote") },
                onRegistrarAlimento = { navController.navigate("registrarAlimento") },
                onAlimentacion = { navController.navigate("alimentacion") },
                onPotrero = { navController.navigate("potrero") },
                onReporteGeneral = { navController.navigate("reporteGeneral") },
                onPerfilUsuario = { navController.navigate("perfil") }
            )
        }

        //  REGISTRAR ANIMAL
        composable("registrarAnimal") {
            RegistrarAnimalScreen(onBack = { navController.popBackStack() })
        }



        //  REGISTRAR LOTE
        composable("registrarLote") {
            RegistrarLoteScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("listaAlimentos") { // O como hayas llamado a esta ruta
            ListaAlimentoScreen(
                onNavigateToRegistro = { navController.navigate("registrarAlimento") }, // Navega a la pantalla de registro
                onBack = { navController.popBackStack() }
            )
        }



        //  REGISTRAR ALIMENTO
        composable("registrarAlimento") {
            RegistrarAlimentoScreen(onBack = { navController.popBackStack() }
            )
        }

        //  ALIMENTACIÓN
        composable("alimentacion") {
            AlimentacionScreen(onBack = { navController.popBackStack() })
        }



        //  POTRERO
        composable("potrero") {
            PotreroScreen(onBack = { navController.popBackStack() })
        }

        //  REPORTES
        composable("reporteGeneral") {
            // Ya no creas el repo aquí, lo reutilizas.
            ReporteGeneralScreen(
                onBack = { navController.popBackStack() },
                repo = repo // <-- Reutiliza el repo
            )
        }


        // GENERAR REPORTE
        composable("generarReporte") {
            GenerarReporteScreen(
                onBack = { navController.popBackStack() },
                repo = repo
            )
        }

//  DETALLE DE REPORTE
        composable("detalleReporte") {
            val reporte = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<com.proyecto_grado.ReporteGeneral>("reporte")

            DetalleReporteScreen(
                reporte = reporte,
                onBack = { navController.popBackStack() }
            )
        }



        // 🔹 PERFIL DE USUARIO
        composable("perfil") {
            PerfilUsuarioScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) // Limpia toda la pila de navegación
                    }
                }
            )
        }
    }
}

@Composable
fun DetalleReporteScreen(reporte: ReporteGeneral?, onBack: () -> Boolean) {
    TODO("Not yet implemented")
}


@Composable
fun AnimalListScreen(onAddAnimalClick: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    TODO("Not yet implemented")
}





