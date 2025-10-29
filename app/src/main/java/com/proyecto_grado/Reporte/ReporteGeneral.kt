package com.proyecto_grado.Reporte

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proyecto_grado.AlimentacionRepository
import com.proyecto_grado.ReporteGeneral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteGeneralScreen(
    onBack: () -> Unit,
    repo: AlimentacionRepository,
    context: Context = LocalContext.current
) {
    val usuarioCorreo = Firebase.auth.currentUser?.email

    // Estados para manejar la UI
    var animalesMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var animalSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var reporteSeleccionado by remember { mutableStateOf<ReporteGeneral?>(null) }

    // Cargar la lista de animales cuando la pantalla se inicia (y el usuario es válido)
    LaunchedEffect(usuarioCorreo) {
        if (usuarioCorreo != null) {
            animalesMap = repo.obtenerAnimales(usuarioCorreo).toMap() // ✅ 3. USAR EL CORREO REAL
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte por Animal", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF80B47A)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Selecciona un Animal",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Si el usuario no está logueado, no mostramos el menú
            if (usuarioCorreo == null) {
                Text("No se pudo identificar al usuario. Por favor, inicie sesión de nuevo.")
            } else {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = animalesMap[animalSeleccionadoId] ?: "Toca para elegir...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (animalesMap.isEmpty()) {
                            DropdownMenuItem(text = { Text("No hay animales registrados") }, onClick = { expanded = false })
                        }
                        animalesMap.forEach { (id, nombre) ->
                            DropdownMenuItem(
                                text = { Text(nombre) },
                                onClick = {
                                    animalSeleccionadoId = id
                                    expanded = false
                                    // Llamar a DB en un hilo de IO
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val reporte = repo.obtenerReportePorAnimalId(id, usuarioCorreo)
                                        withContext(Dispatchers.Main) {
                                            reporteSeleccionado = reporte
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar los datos del reporte si se ha seleccionado un animal
            reporteSeleccionado?.let { reporte ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detalles del Reporte:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Animal: ${reporte.nombreAnimal}")
                        Text("Raza: ${reporte.raza}")
                        Text("Nacimiento: ${reporte.fechaNacimiento}")
                        Text("Peso Actual: ${reporte.pesoActual} kg")
                        Text("Alimento: ${reporte.alimento}")
                        Text("Observación: ${reporte.observacion}")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val csvUri = repo.crearYGuardarReporteCSV(context, reporte)
                        if (csvUri != null) {
                            repo.compartirReportePorWhatsApp(context, csvUri)
                        } else {
                            Toast.makeText(context, "Error al crear el reporte", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Compartir por WhatsApp")
                }
            }
        }
    }
}



