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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.app.DatePickerDialog
import android.widget.DatePicker
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextFieldDefaults




// Modelo de datos del potrero
data class Potrero(
    val numero: String,
    val tipoPasto: String,
    val lote: String,
    val fechaIngreso: String,
    val fechaSalida: String,
    val diasPermanencia: Int,
    val agua: Boolean,
    val observacion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotreroScreen(onBack: () -> Unit) {
    var numero by remember { mutableStateOf("") }
    var tipoPasto by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fechaIngreso by remember { mutableStateOf("") }
    var fechaSalida by remember { mutableStateOf("") }
    var agua by remember { mutableStateOf(false) }
    var observacion by remember { mutableStateOf("") }

    val listaPotreros = remember { mutableStateListOf<Potrero>() }
    val context = LocalContext.current

    // 📅 Función para mostrar el selector de fecha
    fun mostrarDatePicker(onDateSelected: (String) -> Unit) {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val fechaSeleccionada = "%02d/%02d/%d".format(dayOfMonth, month + 1, year)
                onDateSelected(fechaSeleccionada)
            },
            anio,
            mes,
            dia
        ).show()
    }

    // 📆 Función para calcular días de diferencia entre dos fechas
    fun calcularDias(fechaInicio: String, fechaFin: String): Int {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val inicio = formato.parse(fechaInicio)
            val fin = formato.parse(fechaFin)
            val diferencia = fin.time - inicio.time
            (diferencia / (1000 * 60 * 60 * 24)).toInt() // convertir milisegundos a días
        } catch (e: Exception) {
            0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Potrero") },
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
            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Número de potrero") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tipoPasto,
                onValueChange = { tipoPasto = it },
                label = { Text("Tipo de pasto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lote,
                onValueChange = { lote = it },
                label = { Text("Lote") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 📅 Fecha de ingreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDatePicker { fechaIngreso = it } }
            ) {
                OutlinedTextField(
                    value = fechaIngreso,
                    onValueChange = {},
                    label = { Text("Fecha de ingreso") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 📅 Fecha de salida
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDatePicker { fechaSalida = it } }
            ) {
                OutlinedTextField(
                    value = fechaSalida,
                    onValueChange = {},
                    label = { Text("Fecha de salida") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agua,
                    onCheckedChange = { agua = it }
                )
                Text("¿Tiene agua disponible?")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (numero.isNotBlank() && fechaIngreso.isNotBlank() && fechaSalida.isNotBlank()) {
                        val diasPermanencia = calcularDias(fechaIngreso, fechaSalida)

                        listaPotreros.add(
                            Potrero(
                                numero,
                                tipoPasto,
                                lote,
                                fechaIngreso,
                                fechaSalida,
                                diasPermanencia,
                                agua,
                                observacion
                            )
                        )

                        // Limpiar campos
                        numero = ""
                        tipoPasto = ""
                        lote = ""
                        fechaIngreso = ""
                        fechaSalida = ""
                        agua = false
                        observacion = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF80B47A), // verde principal
                    contentColor = Color.White // texto blanco
                )
            )
             {
                Text("Registrar Potrero")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Historial de Potreros", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(listaPotreros) { potrero ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Número: ${potrero.numero}")
                            Text("Pasto: ${potrero.tipoPasto}")
                            Text("Lote: ${potrero.lote}")
                            Text("Ingreso: ${potrero.fechaIngreso}")
                            Text("Salida: ${potrero.fechaSalida}")
                            Text("Días permanencia: ${potrero.diasPermanencia}")
                            Text("Agua: ${if (potrero.agua) "Sí" else "No"}")
                            Text("Observación: ${potrero.observacion}")
                        }
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PotreroScreenPreview() {
    PotreroScreen(onBack = {})
}

