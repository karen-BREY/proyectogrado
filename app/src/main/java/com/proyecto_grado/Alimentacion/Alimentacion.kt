package com.proyecto_grado.Alimentacion


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.proyecto_grado.AlimentacionRepository
import com.proyecto_grado.DatabaseHelper
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



// Modelo de datos (está perfecto)
data class Alimentacion(
    val idAlimentacion: Int = 0,
    val numeroAnimal: Int,
    val lote: Int,
    val alimento: Int,
    val cantidad: Double,
    val frecuencia: String,
    val observacion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ✅ CORREGIDO: Eliminamos el parámetro 'repo' que no se usaba
fun AlimentacionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { AlimentacionRepository(context) }
    val dbHelper = remember { DatabaseHelper(context) }

    val usuarioCorreo = Firebase.auth.currentUser?.email

    // 🔹 Listas y Mapas para los menús desplegables
    val animales = remember { mutableStateListOf<String>() }
    val lotes = remember { mutableStateListOf<String>() }
    val alimentos = remember { mutableStateListOf<String>() }
    val animalesMap = remember { mutableStateMapOf<String, Int>() }
    val lotesMap = remember { mutableStateMapOf<String, Int>() }
    val alimentosMap = remember { mutableStateMapOf<String, Int>() }

    // 🔹 Lista de registros de alimentación
    val listaAlimentacion = remember { mutableStateListOf<Alimentacion>() }

    // 🔹 Cargar datos iniciales solo si el usuario está logueado
    LaunchedEffect(usuarioCorreo) {
        if (usuarioCorreo != null) {
            // Animales
            val animalesList = dbHelper.obtenerAnimalesConId(usuarioCorreo)
            animales.clear(); animalesMap.clear()
            animalesList.forEach { (id, nombre) ->
                animales.add(nombre)
                animalesMap[nombre] = id
            }

            // Lotes
            val lotesList = dbHelper.obtenerLotesConId(usuarioCorreo)
            lotes.clear(); lotesMap.clear()
            lotesList.forEach { (id, nombre) ->
                val nombreLote = "Lote $nombre"
                lotes.add(nombreLote)
                lotesMap[nombreLote] = id
            }

            // Alimentos
            val alimentosList = dbHelper.obtenerAlimentosConId(usuarioCorreo)
            alimentos.clear(); alimentosMap.clear()
            alimentosList.forEach { (id, nombre) ->
                alimentos.add(nombre)
                alimentosMap[nombre] = id
            }

            // Historial
            listaAlimentacion.clear()
            listaAlimentacion.addAll(repo.obtenerAlimentaciones(usuarioCorreo))
        }
    }

    // 🔹 Variables del formulario (sin cambios)
    var animalSeleccionado by remember { mutableStateOf("") }
    var expandedAnimal by remember { mutableStateOf(false) }
    var loteSeleccionado by remember { mutableStateOf("") }
    var expandedLote by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf("") }
    var expandedAlimento by remember { mutableStateOf(false) }
    var cantidad by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF80B47A), // El color verde principal
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Registro de Alimentación") },
                navigationIcon = {
                    IconButton(onClick = onBack) { // La acción para volver atrás
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        // ✅ Usamos LazyColumn para toda la pantalla para evitar que el teclado oculte los campos
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // === FORMULARIO ===
            item {
                // === 🐄 SELECCIONAR ANIMAL ===
                ExposedDropdownMenuBox(
                    expanded = expandedAnimal,
                    onExpandedChange = { expandedAnimal = !expandedAnimal }
                ) {
                    OutlinedTextField(
                        value = animalSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Animal") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedAnimal,
                        onDismissRequest = { expandedAnimal = false }
                    ) {
                        animales.forEach { animal ->
                            DropdownMenuItem(
                                text = { Text(animal) },
                                onClick = {
                                    animalSeleccionado = animal
                                    expandedAnimal = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

// === LOTE ===
                ExposedDropdownMenuBox(
                    expanded = expandedLote,
                    onExpandedChange = { expandedLote = !expandedLote }
                ) {
                    OutlinedTextField(
                        value = loteSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Lote (Opcional)") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedLote,
                        onDismissRequest = { expandedLote = false }
                    ) {
                        lotes.forEach { lote ->
                            DropdownMenuItem(
                                text = { Text(lote) },
                                onClick = {
                                    loteSeleccionado = lote
                                    expandedLote = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

// === ALIMENTO ===
                ExposedDropdownMenuBox(
                    expanded = expandedAlimento,
                    onExpandedChange = { expandedAlimento = !expandedAlimento }
                ) {
                    OutlinedTextField(
                        value = alimentoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Alimento") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedAlimento,
                        onDismissRequest = { expandedAlimento = false }
                    ) {
                        alimentos.forEach { alimento ->
                            DropdownMenuItem(
                                text = { Text(alimento) },
                                onClick = {
                                    alimentoSeleccionado = alimento
                                    expandedAlimento = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad suministrada (Kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = frecuencia,
                    onValueChange = { frecuencia = it },
                    label = { Text("Frecuencia (ej: 'Diaria', '2 veces al día')") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = observacion,
                    onValueChange = { observacion = it },
                    label = { Text("Observación (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )




                Spacer(Modifier.height(16.dp))

                // === BOTÓN GUARDAR ===
                Button(
                    onClick = {
                        val animalId = animalesMap[animalSeleccionado]
                        val loteId = lotesMap[loteSeleccionado]
                        val alimentoId = alimentosMap[alimentoSeleccionado]
                        val cantidadDouble = cantidad.toDoubleOrNull()

                        // ✅ Verificamos también que el correo no sea nulo
                        if (usuarioCorreo != null && animalId != null && alimentoId != null && cantidadDouble != null && cantidadDouble > 0 && frecuencia.isNotBlank()) {
                            val nueva = Alimentacion(
                                numeroAnimal = animalId,
                                lote = loteId ?: 0,
                                alimento = alimentoId,
                                cantidad = cantidadDouble,
                                frecuencia = frecuencia,
                                observacion = observacion
                            )
                            // ✅ Pasamos el correo al insertar
                            repo.insertarAlimentacion(nueva, usuarioCorreo)
                            listaAlimentacion.add(0, nueva) // Añade al principio de la lista para verlo al instante

                            // Limpiar campos
                            animalSeleccionado = ""; loteSeleccionado = ""; alimentoSeleccionado = "";
                            cantidad = ""; frecuencia = ""; observacion = "";

                            Toast.makeText(context, "Registro guardado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = animalSeleccionado.isNotBlank() && alimentoSeleccionado.isNotBlank() && cantidad.isNotBlank() && frecuencia.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8622))
                ) {
                    Text("Registrar Alimentación")
                }

                Spacer(Modifier.height(16.dp))

                // === HISTORIAL ===
                Text("Historial de Alimentación", style = MaterialTheme.typography.titleMedium)
            }

            // === RENDERIZADO DEL HISTORIAL ===
            if (listaAlimentacion.isEmpty()) {
                item { Text("No hay registros aún", modifier = Modifier.padding(8.dp)) }
            } else {
                items(listaAlimentacion) { registro ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                // ✅ CORREGIDO: Usamos los mapas para mostrar nombres, es más eficiente
                                val nombreAnimal = animalesMap.entries.find { it.value == registro.numeroAnimal }?.key ?: "ID: ${registro.numeroAnimal}"
                                val nombreLote = lotesMap.entries.find { it.value == registro.lote }?.key ?: "Sin lote"
                                val nombreAlimento = alimentosMap.entries.find { it.value == registro.alimento }?.key ?: "ID: ${registro.alimento}"

                                Text("Animal: $nombreAnimal", style = MaterialTheme.typography.bodyLarge)
                                Text("Lote: $nombreLote")
                                Text("Alimento: $nombreAlimento")
                                Text("Cantidad: ${registro.cantidad}")
                                Text("Frecuencia: ${registro.frecuencia}")
                                if (registro.observacion.isNotBlank()) {
                                    Text("Obs: ${registro.observacion}")
                                }
                            }

                            IconButton(onClick = {
                                if (usuarioCorreo != null) {
                                    // ✅ Pasamos el correo al eliminar
                                    repo.eliminarAlimentacion(registro.idAlimentacion, usuarioCorreo)
                                    listaAlimentacion.remove(registro)
                                }
                            }) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ✅ Esta pantalla no es necesaria si el historial ya está en AlimentacionScreen,
// pero la corrijo por si la usas en otro sitio.
@Composable
fun ListaAlimentacionScreen(repository: AlimentacionRepository, usuarioCorreo: String?) {
    // ✅ La lista se carga en un LaunchedEffect y depende del correo
    var lista by remember { mutableStateOf<List<Alimentacion>>(emptyList()) }

    LaunchedEffect(usuarioCorreo) {
        if (usuarioCorreo != null) {
            lista = repository.obtenerAlimentaciones(usuarioCorreo)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(lista) { alimentacion ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        // ... (Sería mejor mostrar nombres que IDs aquí también)
                        Text("Animal ID: ${alimentacion.numeroAnimal}")
                        Text("Alimento ID: ${alimentacion.alimento}")
                        Text("Cantidad: ${alimentacion.cantidad}")
                    }
                    IconButton(onClick = {
                        if (usuarioCorreo != null) {
                            repository.eliminarAlimentacion(alimentacion.idAlimentacion, usuarioCorreo)
                            lista = lista.filter { it.idAlimentacion != alimentacion.idAlimentacion } // Actualiza la UI
                        }
                    }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}



