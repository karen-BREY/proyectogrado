// Asegúrate de que el package sea el correcto para tu proyecto
package com.proyecto_grado

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReporteScreen(
    reporte: ReporteGeneral?, // ✅ Recibe el objeto directamente
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // ✅ Si el reporte es nulo, mostramos un mensaje y detenemos la ejecución.
    if (reporte == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Error", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFB00020)) // Color rojo para errores
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No se pudo cargar el detalle del reporte.")
            }
        }
        return
    }

    // --- Si el reporte es válido, se muestra la pantalla normal ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Reporte", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF80B47A))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Usamos 'reporte' directamente, que ya sabemos que no es nulo aquí.
            Text("Nombre: ${reporte.nombreAnimal}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Raza: ${reporte.raza}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha de nacimiento: ${reporte.fechaNacimiento}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Peso actual: ${reporte.pesoActual} kg", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Alimento: ${reporte.alimento}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            if (reporte.observacion.isNotBlank()) {
                Text("Observación: ${reporte.observacion}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { generarYEnviarCSV(context, reporte) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E8622),
                    contentColor = Color.White
                )
            ) {
                Text("Exportar y Enviar CSV")
            }
        }
    }
}

private fun generarYEnviarCSV(context: Context, reporte: ReporteGeneral) {
    try {
        // 1️⃣ Crear el archivo temporal CSV
        val file = File(context.cacheDir, "reporte_${reporte.nombreAnimal}.csv")
        val writer = FileWriter(file)

        // 2️⃣ Escribir encabezados y valores
        writer.appendLine("Campo,Valor")
        writer.appendLine("Nombre,${reporte.nombreAnimal}")
        writer.appendLine("Raza,${reporte.raza}")
        writer.appendLine("Fecha de Nacimiento,${reporte.fechaNacimiento}")
        writer.appendLine("Peso Actual,${reporte.pesoActual}")
        writer.appendLine("Alimento,${reporte.alimento}")
        writer.appendLine("Observación,${reporte.observacion}")
        writer.flush()
        writer.close()

        // 3️⃣ Obtener URI segura con FileProvider
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // 4️⃣ Crear Intent para compartir por WhatsApp
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "📋 Reporte del animal: ${reporte.nombreAnimal}")
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        // En caso de que WhatsApp no esté instalado o haya otro error
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
