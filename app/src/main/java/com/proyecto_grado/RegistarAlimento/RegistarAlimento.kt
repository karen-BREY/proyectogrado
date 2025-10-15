package com.proyecto_grado.RegistarAlimento


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

data class Alimento(
    val nombre: String,
    val cantidad: String,
    val fechaIngreso: String,
    val fechaVencimiento: String,
    val tipo: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAlimentoScreen(onBack: () -> Unit) {
    var mostrarFormulario by remember { mutableStateOf(false) }
    val listaAlimentos = remember { mutableStateListOf<Alimento>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Alimentos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarFormulario = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar alimento",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (mostrarFormulario) {
                FormularioAlimento(
                    onGuardar = { nuevoAlimento ->
                        listaAlimentos.add(nuevoAlimento)
                        mostrarFormulario = false
                    },
                    onCancelar = { mostrarFormulario = false }
                )
            } else {
                if (listaAlimentos.isEmpty()) {
                    Text(
                        text = "No hay alimentos registrados",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn {
                        items(listaAlimentos) { alimento ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${alimento.nombre}", style = MaterialTheme.typography.titleMedium)
                                    Text("Cantidad: ${alimento.cantidad}")
                                    Text("Ingreso: ${alimento.fechaIngreso}")
                                    Text("Vencimiento: ${alimento.fechaVencimiento}")
                                    Text("Tipo: ${alimento.tipo}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FormularioAlimento(
    onGuardar: (Alimento) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var fechaIngreso by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del alimento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad (Kg, L, etc.)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaIngreso,
            onValueChange = { fechaIngreso = it },
            label = { Text("Fecha de ingreso") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaVencimiento,
            onValueChange = { fechaVencimiento = it },
            label = { Text("Fecha de vencimiento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo de alimento (forraje, concentrado, etc.)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onGuardar(
                            Alimento(
                                nombre = nombre,
                                cantidad = cantidad,
                                fechaIngreso = fechaIngreso,
                                fechaVencimiento = fechaVencimiento,
                                tipo = tipo
                            )
                        )
                        // limpiar los campos
                        nombre = ""
                        cantidad = ""
                        fechaIngreso = ""
                        fechaVencimiento = ""
                        tipo = ""
                    }
                }
            ) {
                Text("Guardar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarAlimentoScreenPreview() {
    RegistrarAlimentoScreen(onBack = {})
}
