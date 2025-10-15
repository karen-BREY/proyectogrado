package com.proyecto_grado.Alimentacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color


// Modelo de datos actualizado
data class Alimentacion(
    val numeroAnimal: String,
    val numeroLote: String,
    val alimento: String,
    val cantidad: String,
    val frecuencia: String,
    val observacion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentacionScreen(
    animales: List<String>,   // Lista de animales registrados
    lotes: List<String>,      // Lista de lotes registrados
    alimentos: List<String>,  // Lista de alimentos registrados
    onBack: () -> Unit
) {
    var numeroAnimal by remember { mutableStateOf("") }
    var loteSeleccionado by remember { mutableStateOf("") }
    var alimentoSeleccionado by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    val listaAlimentacion = remember { mutableStateListOf<Alimentacion>() }

    // Estados de expansión de menús
    var expandedLote by remember { mutableStateOf(false) }
    var expandedAlimento by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Alimentación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Campo número de animal
            OutlinedTextField(
                value = numeroAnimal,
                onValueChange = { numeroAnimal = it },
                label = { Text("Número del Animal") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selección de lote
            ExposedDropdownMenuBox(
                expanded = expandedLote,
                onExpandedChange = { expandedLote = !expandedLote }
            ) {
                OutlinedTextField(
                    value = loteSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Lote") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedLote,
                    onDismissRequest = { expandedLote = false }
                ) {
                    lotes.forEach { lote ->
                        DropdownMenuItem(
                            text = { Text(lote) },
                            onClick = {
                                loteSeleccionado = lote
                                expandedLote = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selección de alimento
            ExposedDropdownMenuBox(
                expanded = expandedAlimento,
                onExpandedChange = { expandedAlimento = !expandedAlimento }
            ) {
                OutlinedTextField(
                    value = alimentoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar Alimento") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedAlimento,
                    onDismissRequest = { expandedAlimento = false }
                ) {
                    alimentos.forEach { alimento ->
                        DropdownMenuItem(
                            text = { Text(alimento) },
                            onClick = {
                                alimentoSeleccionado = alimento
                                expandedAlimento = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad suministrada") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Frecuencia
            OutlinedTextField(
                value = frecuencia,
                onValueChange = { frecuencia = it },
                label = { Text("Frecuencia (ej. 2 veces al día)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Observación
            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar
            Button(
                onClick = {
                    if (numeroAnimal.isNotBlank() && loteSeleccionado.isNotBlank() && alimentoSeleccionado.isNotBlank()) {
                        listaAlimentacion.add(
                            Alimentacion(
                                numeroAnimal,
                                loteSeleccionado,
                                alimentoSeleccionado,
                                cantidad,
                                frecuencia,
                                observacion
                            )
                        )
                        // limpiar campos
                        numeroAnimal = ""
                        loteSeleccionado = ""
                        alimentoSeleccionado = ""
                        cantidad = ""
                        frecuencia = ""
                        observacion = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E8622), // verde principal
                    contentColor = Color.White // texto blanco
                )
            ) {
                Text("Registrar Alimentación")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Historial de Alimentación", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(listaAlimentacion) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Animal: ${registro.numeroAnimal}")
                            Text("Lote: ${registro.numeroLote}")
                            Text("Alimento: ${registro.alimento}")
                            Text("Cantidad: ${registro.cantidad}")
                            Text("Frecuencia: ${registro.frecuencia}")
                            if (registro.observacion.isNotBlank()) {
                                Text("Observación: ${registro.observacion}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlimentacionScreenPreview() {
    val animalesEjemplo = listOf("001", "002", "003")
    val lotesEjemplo = listOf("Lote 1", "Lote 2", "Lote 3")
    val alimentosEjemplo = listOf("Pasto kikuyo", "Melaza", "Concentrado")

    AlimentacionScreen(
        animales = animalesEjemplo,
        lotes = lotesEjemplo,
        alimentos = alimentosEjemplo,
        onBack = {}
    )
}
