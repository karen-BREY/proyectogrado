package com.proyecto_grado.RegistroAnimal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun RegistrarAnimalScreen(onBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var pesoActual by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

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
        OutlinedTextField(value = lote, onValueChange = { lote = it }, label = { Text("Lote") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha nacimiento") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pesoActual, onValueChange = { pesoActual = it }, label = { Text("Peso actual") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = observaciones, onValueChange = { observaciones = it }, label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Aquí luego conectamos con Firebase o BD
                println("Animal registrado: $nombre, $raza")
            },
            modifier = Modifier.fillMaxWidth()
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
        RegistrarAnimalScreen(onBack = {})
    }
}