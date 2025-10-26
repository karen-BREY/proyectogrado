package com.proyecto_grado.RegistroAnimal

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import java.util.Calendar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import com.proyecto_grado.AnimalRepository
import com.proyecto_grado.Animal
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.proyecto_grado.DatabaseHelper




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAnimalScreen(onBack: () -> Unit) {
    var mostrarFormulario by remember { mutableStateOf(false) }
    var animalEditar by remember { mutableStateOf<Animal?>(null) }
    val listaAnimales = remember { mutableStateListOf<Animal>() }

    val context = LocalContext.current
    val verdePrincipal = Color(0xFF80B47A)
    val repo = remember { AnimalRepository(context) }
    val dbHelper = remember { DatabaseHelper(context) }

    // ✅ Obtenemos el correo del usuario actual para filtrar los datos
    val usuarioCorreo = Firebase.auth.currentUser?.email

    var lotes by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }


    LaunchedEffect(mostrarFormulario, usuarioCorreo) {
        // ✅ Solo ejecutamos si el usuario está logueado
        if (usuarioCorreo != null) {
            if (!mostrarFormulario) {
                listaAnimales.clear()
                // ✅ Pasamos el correo para obtener solo los animales de este usuario
                listaAnimales.addAll(repo.obtenerAnimales(usuarioCorreo))
            }
            // ✅ Pasamos el correo para obtener solo los lotes de este usuario
            lotes = dbHelper.obtenerLotesConId(usuarioCorreo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Animales", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = verdePrincipal)
            )
        },
        floatingActionButton = {
            if (!mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        animalEditar = null
                        mostrarFormulario = true
                    },
                    containerColor = verdePrincipal
                ) { Icon(Icons.Default.Add, "Agregar animal", tint = Color.White) }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .testTag("pantalla_registro_animal") // <-- TEST TAG AÑADIDO AQUÍ
        ) {
            if (mostrarFormulario) {
                FormularioAnimal(
                    animal = animalEditar,
                    lotesDisponibles = lotes,
                    onGuardar = { nuevoAnimal ->
                        if (usuarioCorreo != null) {
                            if (animalEditar == null) {

                                repo.insertarAnimal(nuevoAnimal, usuarioCorreo)
                            } else {

                                repo.actualizarAnimal(nuevoAnimal, usuarioCorreo)
                            }
                            mostrarFormulario = false
                            Toast.makeText(context, "Animal guardado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Error: No se pudo identificar al usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onCancelar = { mostrarFormulario = false }
                )
            } else {
                if (listaAnimales.isEmpty()) {
                    Text(
                        "No hay animales registrados",
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
                    )
                } else {
                    LazyColumn {
                        items(listaAnimales) { animal ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "🐄 ${animal.nombre} (${animal.raza})",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("Número: ${animal.numero}")

                                        //
                                        val nombreLote =
                                            lotes.find { it.first == animal.lote }?.second
                                                ?: "Sin lote"
                                        Text(if (nombreLote != "Sin lote") "Lote: $nombreLote" else "Sin lote asignado")

                                        Text("Fecha nac.: ${animal.fechaNacimiento}")
                                        Text("Edad: ${animal.edad}")
                                        Text("Peso actual: ${animal.pesoActual} kg")
                                        if (animal.observaciones.isNotBlank()) {
                                            Text("Obs.: ${animal.observaciones}")
                                        }
                                    }
                                    IconButton(onClick = {
                                        animalEditar = animal
                                        mostrarFormulario = true
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            "Editar",
                                            tint = Color(0xFF4CAF50)
                                        )
                                    }

                                    IconButton(onClick = {
                                        if (usuarioCorreo != null) {
                                            repo.eliminarAnimal(animal.id, usuarioCorreo)
                                            listaAnimales.remove(animal)
                                        }
                                    }) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioAnimal(
    onGuardar: (Animal) -> Unit,
    onCancelar: () -> Unit,
    animal: Animal?,
    lotesDisponibles: List<Pair<Int, String>> //
) {
    val context = LocalContext.current
    val verdePrincipal = Color(0xFF80B47A)

    var nombre by remember { mutableStateOf(animal?.nombre ?: "") }
    var numero by remember { mutableStateOf(animal?.numero?.toString() ?: "") }
    var loteId by remember { mutableStateOf(animal?.lote) }
    var fechaNacimiento by remember { mutableStateOf(animal?.fechaNacimiento ?: "") }
    var edad by remember { mutableStateOf(animal?.edad ?: "") }
    var raza by remember { mutableStateOf(animal?.raza ?: "") }
    var pesoActual by remember { mutableStateOf(animal?.pesoActual?.toString() ?: "") }
    var observaciones by remember { mutableStateOf(animal?.observaciones ?: "") }


    var loteSeleccionadoTexto by remember {
        mutableStateOf(
            if (animal?.lote != null) {
                "Lote " + (lotesDisponibles.find { it.first == animal.lote }?.second ?: "")
            } else {
                ""
            }
        )
    }

    var expanded by remember { mutableStateOf(false) }

    // === CONFIGURAR CALENDARIO ===
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                fechaNacimiento = "%02d/%02d/%d".format(day, month + 1, year)

                val nacimiento = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val hoy = Calendar.getInstance()

                var años = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
                var meses = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH)
                var dias = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH)

                if (dias < 0) {
                    meses -= 1
                    dias += hoy.getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                if (meses < 0) {
                    años -= 1
                    meses += 12
                }

                edad = when {
                    años > 0 && meses > 0 -> "$años año${if (años > 1) "s" else ""} y $meses mes${if (meses > 1) "es" else ""}"
                    años > 0 && meses == 0 -> "$años año${if (años > 1) "s" else ""}"
                    años == 0 && meses > 0 -> "$meses mes${if (meses > 1) "es" else ""}"
                    else -> "$dias día${if (dias > 1) "s" else ""}"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // === FORMULARIO ===
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del animal") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Número de identificación") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // === MENÚ DESPLEGABLE DE LOTE ===
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = loteSeleccionadoTexto, // ✅ Muestra el texto del lote
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar lote") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                lotesDisponibles.forEach { (id, numeroLote) ->
                    DropdownMenuItem(
                        text = { Text("Lote $numeroLote") },
                        onClick = {
                            loteId = id
                            loteSeleccionadoTexto = "Lote $numeroLote"
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // === FECHA DE NACIMIENTO ===
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {},
            label = { Text("Fecha de nacimiento") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = {},
            label = { Text("Edad (calculada)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = raza,
            onValueChange = { raza = it },
            label = { Text("Raza") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pesoActual,
            onValueChange = { pesoActual = it },
            label = { Text("Peso actual (Kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // === BOTONES ===
        Row {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        val numeroInt = numero.toIntOrNull()
                        val pesoDouble = pesoActual.toDoubleOrNull()

                        if (nombre.isBlank() || numeroInt == null || pesoDouble == null || loteId == null) {
                            Toast.makeText(context, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val animalAGuardar = Animal(
                            id = animal?.id ?: 0,
                            nombre = nombre,
                            numero = numeroInt,
                            lote = loteId!!,
                            fechaNacimiento = fechaNacimiento,
                            edad = edad,
                            raza = raza,
                            pesoActual = pesoDouble ?: 0.0,
                            observaciones = observaciones
                        )
                        onGuardar(animalAGuardar)

                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingresa el nombre del animal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = verdePrincipal)
            ) {
                Text("Guardar", color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onCancelar() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun RegistrarAnimalScreenPreview() {
    RegistrarAnimalScreen(onBack = {})
}






