package com.proyecto_grado.RegistarAlimento


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember




@Composable
fun ListaAlimentoScreen(
    alimentos: List<String>,
    onRegistrarAlimento: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Lista de Alimentos", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (alimentos.isEmpty()) {
            Text("No hay alimentos registrados.")
        } else {
            alimentos.forEach { alimento ->
                Text(alimento, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegistrarAlimento,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar nuevo alimento")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Volver")
        }
    }
}
