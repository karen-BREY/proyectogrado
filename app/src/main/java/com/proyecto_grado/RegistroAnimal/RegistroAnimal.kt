package com.proyecto_grado.RegistroAnimal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.Color



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAnimalScreen(
    onBack: () -> Unit,
    lotes: List<String> = emptyList() // 🔹 Lista de lotes disponibles
) {
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var pesoActual by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) } // 🔹 Control del menú desplegable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Registrar Animal", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = numero, onValueChange = { numero = it }, label = { Text("Número") }, modifier = Modifier.fillMaxWidth())

        // 🔹 Selector de Lote
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = lote,
                onValueChange = { },
                readOnly = true,
                label = { Text("Lote") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                lotes.forEach { loteItem ->
                    DropdownMenuItem(
                        text = { Text(loteItem) },
                        onClick = {
                            lote = loteItem
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha nacimiento") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pesoActual, onValueChange = { pesoActual = it }, label = { Text("Peso actual") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = observaciones, onValueChange = { observaciones = it }, label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Aquí luego conectamos con Firebase o BD
                println("Animal registrado: $nombre, $raza en lote: $lote")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E8622), // verde principal
                contentColor = Color.White // texto blanco
            )
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarAnimalScreenPreview() {
    MaterialTheme {
        RegistrarAnimalScreen(
            onBack = {},
            lotes = listOf("Lote 1", "Lote 2", "Lote 3") // 🔹 Ejemplo
        )
    }
}
