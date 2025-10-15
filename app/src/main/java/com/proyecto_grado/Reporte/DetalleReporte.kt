package com.proyecto_grado.DetalleReporte

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color


// Modelo de datos del reporte
data class ReporteAnimal(
    val nombre: String,
    val raza: String,
    val fechaNacimiento: String,
    val pesoActual: String,
    val variacionPeso: String,
    val tipoPasto: String,
    val suplemento: String,
    val observaciones: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReporteScreen(
    onBack: () -> Unit,
    reporte: ReporteAnimal
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Reporte") },
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
            Text("Detalle del Animal", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nombre: ${reporte.nombre}")
            Text("Raza: ${reporte.raza}")
            Text("Fecha de Nacimiento: ${reporte.fechaNacimiento}")
            Text("Peso actual: ${reporte.pesoActual} kg")
            Text("Variación de peso: ${reporte.variacionPeso} kg")
            Text("Tipo de pasto: ${reporte.tipoPasto}")
            Text("Suplemento: ${reporte.suplemento}")
            Text("Observaciones: ${reporte.observaciones}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { generarYCompartirPDF(context, reporte) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E8622), // verde principal
                    contentColor = Color.White // texto blanco
                )
            ) {
                Text(" Exportar y Enviar por WhatsApp")
            }
        }
    }
}

private fun generarYCompartirPDF(
    context: Context,
    animal: ReporteAnimal
) {
    // Aquí implementaremos la generación y envío del PDF
    val mensaje = """
        Reporte del Animal:
        Nombre: ${animal.nombre}
        Raza: ${animal.raza}
        Peso actual: ${animal.pesoActual} kg
        Variación de peso: ${animal.variacionPeso} kg
        Tipo de pasto: ${animal.tipoPasto}
        Suplemento: ${animal.suplemento}
        Observaciones: ${animal.observaciones}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, mensaje)
        setPackage("com.whatsapp")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Si no está instalado WhatsApp
        e.printStackTrace()
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetalleReportePreview() {
    val reporteEjemplo = ReporteAnimal(
        nombre = "Toro Bravo",
        raza = "Brahman",
        fechaNacimiento = "12/05/2022",
        pesoActual = "450",
        variacionPeso = "+20",
        tipoPasto = "Estrella Africana",
        suplemento = "Concentrado Premium",
        observaciones = "Buen aumento de peso"
    )

    DetalleReporteScreen(
        onBack = {},
        reporte = reporteEjemplo
    )
}
