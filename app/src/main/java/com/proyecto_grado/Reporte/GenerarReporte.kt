package com.proyecto_grado.Reporte

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.proyecto_grado.AlimentacionRepository
import com.proyecto_grado.ReporteGeneral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerarReporteScreen(
    onBack: () -> Unit,
    repo: AlimentacionRepository,
    context: Context = LocalContext.current
) {
    val usuarioCorreo = Firebase.auth.currentUser?.email ?: ""
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Reporte CSV") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        // ✅ PASO 2: Validar que el correo no sea nulo.
                        if (usuarioCorreo == null) {
                            Toast.makeText(context, "Error: No se pudo identificar al usuario.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            // ✅ PASO 3: Pasar el correo correcto a la función del repositorio.
                            val lista = repo.obtenerReporteGeneral(usuarioCorreo)
                            val csvFile = generarCSV(context, lista)
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                if (lista.isNotEmpty()) {
                                    enviarPorWhatsApp(context, csvFile)
                                } else {
                                    Toast.makeText(context, "No hay datos de animales para generar el reporte.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    // Deshabilitamos el botón si no hay usuario logueado
                    enabled = usuarioCorreo != null
                ) {
                    Text("Generar y Enviar CSV")
                }
            }
        }
    }
}

fun generarCSV(context: Context, lista: List<ReporteGeneral>): File {
    val file = File(context.cacheDir, "reporte_animales.csv")
    file.bufferedWriter().use { out ->
        out.write("Nombre,Raza,Peso,FechaNacimiento,Alimento\n")
        lista.forEach { item ->
            out.write("${item.nombreAnimal},${item.raza},${item.pesoActual},${item.fechaNacimiento},${item.alimento}\n")
        }
    }
    return file
}


fun enviarPorWhatsApp(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        setPackage("com.whatsapp")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp no instalado", Toast.LENGTH_SHORT).show()
    }
}


