package com.proyecto_grado.Perfil


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.proyecto_grado.Perfil.PerfilRepository
import com.proyecto_grado.Perfil.Usuario


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var mensajeCarga by remember { mutableStateOf("Cargando...") }

    val context = LocalContext.current
    val perfilRepo = remember { PerfilRepository(context) }

    LaunchedEffect(Unit) {
        val emailUsuarioActual = Firebase.auth.currentUser?.email
        if (emailUsuarioActual != null) {
            val datosUsuario = perfilRepo.obtenerUsuarioPorCorreo(emailUsuarioActual)
            if (datosUsuario != null) {
                usuario = datosUsuario
            } else {
                mensajeCarga = "Usuario no encontrado"
            }
        } else {
            mensajeCarga = "No se pudo identificar al usuario"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // === FRANJA VERDE CON NOMBRE Y CORREO ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4CAF50))
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Icono de Perfil",
                    modifier = Modifier.size(90.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (usuario != null) "${usuario?.nombre} ${usuario?.apellido}" else mensajeCarga,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = usuario?.correo ?: "",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // === DETALLES DEL PERFIL ===
            if (usuario != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PerfilDatoItem("Nombre", usuario!!.nombre)
                    PerfilDatoItem("Apellido", usuario!!.apellido)
                    PerfilDatoItem("Teléfono", usuario!!.telefono)
                    PerfilDatoItem("Correo", usuario!!.correo)
                    PerfilDatoItem("Contraseña", "********") // ocultamos por seguridad
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))
                Text(mensajeCarga, color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            // === BOTÓN DE CERRAR SESIÓN ===
            Box(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

// Reutilizable: para mostrar una fila de "Etiqueta: valor"
@Composable
fun PerfilDatoItem(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
        Text(valor, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilUsuarioPreview() {
    PerfilUsuarioScreen(
        onBack = {},
        onLogout = {}
    )
}

