package com.proyecto_grado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaAlimentoScreen(
    onNavigateToRegistro: () -> Unit,
    onBack: () -> Unit
) {
    // --- LÓGICA PARA OBTENER DATOS ---
    val context = LocalContext.current
    val alimentoRepository = remember { AlimentoRepository(context) }
    val usuarioCorreo = Firebase.auth.currentUser?.email
    val listaAlimentos = remember { mutableStateListOf<Alimento>() }

    // Función para recargar la lista desde la base de datos
    fun recargarAlimentos() {
        if (usuarioCorreo != null) {
            listaAlimentos.clear()
            listaAlimentos.addAll(alimentoRepository.obtenerAlimentos(usuarioCorreo))
        }
    }

    // Carga los alimentos cuando la pantalla aparece por primera vez
    LaunchedEffect(usuarioCorreo) {
        recargarAlimentos()
    }

    // --- INTERFAZ DE USUARIO ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Alimentos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (listaAlimentos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay alimentos registrados.")
                }
            } else {
                // ✅ Usamos LazyColumn para mostrar la lista de objetos 'Alimento'
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listaAlimentos) { alimento ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Nombre: ${alimento.nombre}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Cantidad: ${alimento.cantidad}")
                                Text("Tipo: ${alimento.tipo}")
                                Text("Vence: ${alimento.fechaVencimiento}")

                                // Botón para eliminar
                                IconButton(
                                    onClick = {
                                        alimentoRepository.eliminarAlimento(alimento.id, usuarioCorreo!!)
                                        recargarAlimentos() // Actualiza la lista en la UI
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToRegistro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Nuevo Alimento")
            }
        }
    }
}

