package com.proyecto_grado.RegistroLote.kt


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color



@Composable
fun RegistrarLoteScreen(
    onBack: () -> Unit,
    onLoteGuardado: (String, String) -> Unit // número, observación
) {
    var numero by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // Lista de lotes guardados temporal (podrás reemplazarla con datos de SQLite luego)
    var listaLotes by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Registrar Lote",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Campo número
        TextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Número del Lote") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo observación
        TextField(
            value = observacion,
            onValueChange = { observacion = it },
            label = { Text("Observación") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón guardar
        Button(
            onClick = {
                if (numero.isBlank()) {
                    error = "Por favor ingresa el número del lote"
                } else {
                    error = ""
                    onLoteGuardado(numero, observacion)
                    // Guardar en la lista temporal
                    listaLotes = listaLotes + (numero to observacion)
                    // Limpiar campos
                    numero = ""
                    observacion = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E8622), // verde principal
                contentColor = Color.White // texto blanco
            )
        ) {
            Text("Guardar Lote")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón volver
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mostrar lista de lotes guardados
        if (listaLotes.isNotEmpty()) {
            Text(
                text = "Lotes registrados:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            listaLotes.forEach { (num, obs) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Número: $num", style = MaterialTheme.typography.bodyLarge)
                        if (obs.isNotBlank()) {
                            Text("Observación: $obs", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrarLotePreview() {
    MaterialTheme {
        RegistrarLoteScreen(
            onBack = {},
            onLoteGuardado = { _, _ -> }
        )
    }
}
