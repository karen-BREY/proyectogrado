package com.proyecto_grado.RegistroLote


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proyecto_grado.Lote
import com.proyecto_grado.LoteRepository
import androidx.compose.foundation.lazy.items








@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarLoteScreen(
    onBack: () -> Unit

) {
    val context = LocalContext.current
    val loteRepository = remember { LoteRepository(context) }
    val verdePrincipal = Color(0xFF80B47A)

    // ✅ PASO 1: Obtener el correo del usuario actual.
    val usuarioCorreo = Firebase.auth.currentUser?.email

    var numero by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    val listaLotes = remember { mutableStateListOf<Lote>() }

    // ✅ PASO 2: Cargar los lotes del usuario al iniciar la pantalla.
    LaunchedEffect(usuarioCorreo) {
        if (usuarioCorreo != null) {
            listaLotes.clear()
            listaLotes.addAll(loteRepository.obtenerLotes(usuarioCorreo))
        }
    }

    // ✅ Usamos Scaffold para un diseño consistente.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Lotes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = verdePrincipal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- FORMULARIO ---
            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Número del Lote") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observación (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val numeroInt = numero.toIntOrNull()

                    // ✅ PASO 3: Validar y pasar el correo al guardar.
                    if (numeroInt != null && usuarioCorreo != null) {
                        val nuevoLote = Lote(numero = numeroInt, observacion = observacion)
                        loteRepository.insertarLote(nuevoLote, usuarioCorreo)

                        // Recargamos la lista desde la base de datos para obtener el lote con su ID.
                        listaLotes.clear()
                        listaLotes.addAll(loteRepository.obtenerLotes(usuarioCorreo))

                        // Limpiamos los campos.
                        numero = ""
                        observacion = ""
                        Toast.makeText(context, "Lote guardado correctamente", Toast.LENGTH_SHORT)
                            .show()

                    } else if (usuarioCorreo == null) {
                        Toast.makeText(
                            context,
                            "Error: No se pudo identificar al usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingresa un número de lote válido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8622))
            ) {
                Text("Guardar Lote")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- HISTORIAL DE LOTES ---
            Text("Lotes Registrados", style = MaterialTheme.typography.titleMedium)

            if (listaLotes.isEmpty()) {
                Text("No hay lotes registrados aún.", modifier = Modifier.padding(top = 8.dp))
            } else {
                LazyColumn {
                    items(listaLotes) { lote ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Lote Número: ${lote.numero}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (!lote.observacion.isNullOrBlank()) {
                                        Text("Observación: ${lote.observacion}")
                                    }
                                }
                                IconButton(onClick = {
                                    if (usuarioCorreo != null) {
                                        loteRepository.eliminarLote(lote.idLote, usuarioCorreo)
                                        listaLotes.remove(lote)
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color(0xFFFF0000)

                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarLotePreview() {
    MaterialTheme {
        RegistrarLoteScreen(onBack = {})
    }
}

