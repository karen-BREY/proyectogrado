package com.proyecto_grado.Potrero

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.widget.DatePicker
import android.widget.Toast
import java.util.Calendar
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import com.proyecto_grado.DatabaseHelper
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.proyecto_grado.LoteRepository
import java.util.Date
import java.util.Locale

data class Potrero(
    val idPotrero: Int,
    val numero: Int,
    val tipoPasto: String,
    val lote: Int?,
    val fechaIngreso: String?,
    val fechaSalida: String?,
    val observacion: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotreroScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { PotreroRepository(context) }
    val dbHelper = remember { DatabaseHelper(context) }
    val usuarioCorreo = Firebase.auth.currentUser?.email

    val lotes = remember { mutableStateListOf<String>() }
    val lotesMap = remember { mutableStateMapOf<String, Int>() }
    val listaPotreros = remember { mutableStateListOf<Potrero>() }

    var numero by remember { mutableStateOf("") }
    var tipoPasto by remember { mutableStateOf("") }
    var loteSeleccionado by remember { mutableStateOf("") }
    var expandedLote by remember { mutableStateOf(false) }
    var fechaIngreso by remember { mutableStateOf("") }
    var fechaSalida by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    var diasPermanencia by remember { mutableStateOf("") }
    var proximoPotrero by remember { mutableStateOf("") }


    fun calcularDias(ingreso: String, salida: String): Int {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaI = formato.parse(ingreso)
            val fechaS = formato.parse(salida)
            val diff = fechaS.time - fechaI.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            -1
        }
    }


    fun diasRestantesHastaSalida(salida: String?): Int {
        return try {
            if (salida.isNullOrEmpty()) return -1
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaS = formato.parse(salida)
            val hoy = Date()
            val diff = fechaS.time - hoy.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            -1
        }
    }

    fun mostrarDatePicker(onDateSelected: (String) -> Unit) {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                onDateSelected("%02d/%02d/%d".format(day, month + 1, year))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(usuarioCorreo) {
        if (usuarioCorreo != null) {
            listaPotreros.clear()
            listaPotreros.addAll(repo.obtenerPotreros(usuarioCorreo))

            val loteRepo = LoteRepository(context)
            lotes.clear()
            lotesMap.clear()
            loteRepo.obtenerLotes(usuarioCorreo).forEach { lote ->
                val nombre = "Lote ${lote.numero}"
                lotes.add(nombre)
                lotesMap[nombre] = lote.idLote
            }

            // 🔹 Calcular sugerencia del próximo potrero
            val ultimoNumero = listaPotreros.maxOfOrNull { it.numero } ?: 0
            proximoPotrero = (ultimoNumero + 1).toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Potrero") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF80B47A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // === FORMULARIO ===
            item {
                OutlinedTextField(
                    value = numero,
                    onValueChange = { numero = it },
                    label = { Text("Número de potrero") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (proximoPotrero.isNotBlank()) {
                    Text(
                        text = "💡 Sugerencia: próximo potrero N° $proximoPotrero",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = tipoPasto,
                    onValueChange = { tipoPasto = it },
                    label = { Text("Tipo de pasto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // SELECCIONAR LOTE
                ExposedDropdownMenuBox(
                    expanded = expandedLote,
                    onExpandedChange = { expandedLote = !expandedLote }
                ) {
                    OutlinedTextField(
                        value = loteSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Lote") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expandedLote,
                        onDismissRequest = { expandedLote = false }
                    ) {
                        if (lotes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay lotes registrados") },
                                onClick = { expandedLote = false }
                            )
                        } else {
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
                }

                Spacer(Modifier.height(8.dp))

                // === FECHAS ===
                OutlinedTextField(
                    value = fechaIngreso,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de ingreso") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                mostrarDatePicker {
                                    fechaIngreso = it
                                    if (fechaSalida.isNotBlank()) {
                                        diasPermanencia = "${calcularDias(it, fechaSalida)} días"
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = fechaSalida,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de salida") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                mostrarDatePicker {
                                    fechaSalida = it
                                    if (fechaIngreso.isNotBlank()) {
                                        diasPermanencia = "${calcularDias(fechaIngreso, it)} días"
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (diasPermanencia.isNotBlank()) {
                    Text(
                        text = "Días de permanencia: $diasPermanencia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1E8622),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = observacion,
                    onValueChange = { observacion = it },
                    label = { Text("Observación (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val loteId = lotesMap[loteSeleccionado]
                        val numeroInt = numero.toIntOrNull()

                        if (usuarioCorreo != null && numeroInt != null && fechaIngreso.isNotBlank() && fechaSalida.isNotBlank()) {
                            val nuevo = Potrero(
                                idPotrero = 0,
                                numero = numeroInt,
                                tipoPasto = tipoPasto.ifBlank { "Sin especificar" },
                                lote = loteId,
                                fechaIngreso = fechaIngreso,
                                fechaSalida = fechaSalida,
                                observacion = observacion.ifBlank { "Sin observación" }
                            )
                            repo.insertarPotrero(nuevo, usuarioCorreo)
                            listaPotreros.add(0, nuevo)

                            proximoPotrero = (numeroInt + 1).toString()

                            numero = ""
                            tipoPasto = ""
                            loteSeleccionado = ""
                            fechaIngreso = ""
                            fechaSalida = ""
                            observacion = ""
                            diasPermanencia = ""

                            Toast.makeText(context, "Potrero registrado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8622))
                ) {
                    Text("Registrar Potrero")
                }

                Spacer(Modifier.height(16.dp))

                Text("Lista de Potreros", style = MaterialTheme.typography.titleMedium)
            }

            // === LISTA DE POTREROS ===
            if (listaPotreros.isEmpty()) {
                item { Text("No hay potreros registrados", modifier = Modifier.padding(8.dp)) }
            } else {
                items(listaPotreros) { potrero ->
                    val diasRestantes = diasRestantesHastaSalida(potrero.fechaSalida)
                    val mensajeTraslado = when {
                        diasRestantes > 0 -> "En $diasRestantes días se pasa al potrero ${potrero.numero + 1}"
                        diasRestantes == 0 -> "Hoy se realiza el traslado"
                        diasRestantes < 0 -> "Traslado pendiente"
                        else -> ""
                    }

                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                val nombreLote = lotesMap.entries.find { it.value == potrero.lote }?.key ?: "Sin lote"

                                Text("Potrero N°: ${potrero.numero}", style = MaterialTheme.typography.bodyLarge)
                                Text("Tipo de pasto: ${potrero.tipoPasto}")
                                Text("Lote: $nombreLote")
                                Text("Ingreso: ${potrero.fechaIngreso}")
                                Text("Salida: ${potrero.fechaSalida}")
                                if (!potrero.observacion.isNullOrBlank()) Text("Obs: ${potrero.observacion}")
                                if (mensajeTraslado.isNotBlank()) {
                                    Text(
                                        mensajeTraslado,
                                        color = Color(0xFF388E3C),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            IconButton(onClick = {
                                if (usuarioCorreo != null) {
                                    repo.eliminarPotrero(potrero.idPotrero, usuarioCorreo)
                                    listaPotreros.remove(potrero)
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








