package com.proyecto_grado.Reporte

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import androidx.navigation.NavController
import com.proyecto_grado.DetalleReporte.ReporteAnimal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteScreen(navController: NavController, onBack: () -> Unit) {
    // 🔹 Lista simulada de reportes (puedes conectarla luego con tu base de datos)
    val listaReportes = remember {
        listOf(
            ReporteAnimal(
                nombre = "Toro A",
                raza = "Brahman",
                fechaNacimiento = "2023-01-10",
                pesoActual = "450",
                variacionPeso = "15",
                tipoPasto = "Angleton",
                suplemento = "Melaza",
                observaciones = "Buen aumento de peso"
            ),
            ReporteAnimal(
                nombre = "Vaca B",
                raza = "Holstein",
                fechaNacimiento = "2022-08-20",
                pesoActual = "520",
                variacionPeso = "-5",
                tipoPasto = "Estrella",
                suplemento = "Sal mineralizada",
                observaciones = "Leve pérdida de peso"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes de Animales") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(listaReportes) { reporte ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            // 🔹 Convierte el objeto en JSON y navega al detalle
                            val gson = Gson()
                            val reporteJson = gson.toJson(reporte)
                            navController.navigate("detalleReporte/$reporteJson")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${reporte.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Raza: ${reporte.raza}")
                        Text("Peso actual: ${reporte.pesoActual} kg")
                        Text("Variación: ${reporte.variacionPeso} kg")
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReporteScreenPreview() {
    ReporteScreen(
        navController = rememberNavController(),
        onBack = {}
    )
}
