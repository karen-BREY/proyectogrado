package com.proyecto_grado.RegistarAlimento


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import java.util.Calendar
import androidx.compose.foundation.clickable
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import com.proyecto_grado.AlimentoRepository
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.proyecto_grado.Alimento

data class Alimento(
    val id: Int = 0,
    val nombre: String,
    val cantidad: String,
    val fechaIngreso: String,
    val fechaVencimiento: String,
    val tipo: String
)


@Preview(showBackground = true)
@Composable
fun RegistrarAlimentoScreenPreview() {
    RegistrarAlimentoScreen(onBack = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAlimentoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { AlimentoRepository(context) }

    // ✅ Obtenemos el correo del usuario actual.
    val usuarioCorreo = Firebase.auth.currentUser?.email

    var mostrarFormulario by remember { mutableStateOf(false) }
    val listaAlimentos = remember { mutableStateListOf<Alimento>() }
    var alimentoEditando by remember { mutableStateOf<Alimento?>(null) }

    // --- Función para recargar los datos ---
    // La usaremos varias veces, así que es mejor tenerla en una función.
    fun recargarAlimentos() {
        if (usuarioCorreo != null) {
            listaAlimentos.clear()
            // ✅ Paréntesis de cierre añadido
            listaAlimentos.addAll(repo.obtenerAlimentos(usuarioCorreo).map {
                Alimento(it.id, it.nombre, it.cantidad, it.fechaIngreso, it.fechaVencimiento, it.tipo)
            })
        }
    }

    // --- Efectos de carga ---
    LaunchedEffect(Unit) {
        if (usuarioCorreo != null) {
            listaAlimentos.clear()
            listaAlimentos.addAll(
                repo.obtenerAlimentos(usuarioCorreo).map {
                    Alimento(it.id, it.nombre, it.cantidad, it.fechaIngreso, it.fechaVencimiento, it.tipo)
                }
            )
        }
    }


    // Cargar alimentos cuando la pantalla se abre por primera vez o se cierra el formulario.
    LaunchedEffect(mostrarFormulario) {
        if (!mostrarFormulario && usuarioCorreo != null) {
            recargarAlimentos()
            // Comprobación de alimentos por vencer
            val alimentosPorVencer = repo.obtenerAlimentosPorVencer(usuarioCorreo)
            if (alimentosPorVencer.isNotEmpty()) {
                val mensaje = alimentosPorVencer.joinToString("\n") { it.nombre }
                // mostrarNotificacion(context, "Alimentos próximos a vencer", mensaje) // Descomentar si tienes la función de notificación
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Alimentos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF80B47A))
            )
        },
        floatingActionButton = {
            if (!mostrarFormulario) { // Solo mostrar el botón si no estamos en el formulario
                FloatingActionButton(
                    onClick = {
                        alimentoEditando = null // Limpiamos la edición
                        mostrarFormulario = true
                    },
                    containerColor = Color(0xFF80B47A)
                ) {
                    Icon(Icons.Default.Add, "Agregar alimento", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (mostrarFormulario) {
                FormularioAlimento(
                    alimentoInicial = alimentoEditando, // Pasamos el alimento a editar
                    onGuardar = { alimentoAGuardar ->
                        if (usuarioCorreo != null) {
                            if (alimentoAGuardar.id == 0) { // Si el ID es 0, es un nuevo alimento
                                repo.insertarAlimento(alimentoAGuardar, usuarioCorreo)
                            } else { // Si tiene ID, es una actualización
                                repo.actualizarAlimento(alimentoAGuardar, usuarioCorreo)
                            }
                            mostrarFormulario = false // Cierra el formulario y dispara el LaunchedEffect para recargar
                            Toast.makeText(context, "Alimento guardado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCancelar = { mostrarFormulario = false }
                )
            } else {
                if (listaAlimentos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay alimentos registrados")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        items(listaAlimentos) { alimento ->
                            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Nombre: ${alimento.nombre}", style = MaterialTheme.typography.titleMedium)
                                        Text("Cantidad: ${alimento.cantidad}")
                                        Text("Ingreso: ${alimento.fechaIngreso}")
                                        Text("Vencimiento: ${alimento.fechaVencimiento}")
                                        Text("Tipo: ${alimento.tipo}")
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            alimentoEditando = alimento
                                            mostrarFormulario = true
                                        }) {
                                            Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF4CAF50))
                                        }
                                        IconButton(onClick = {
                                            if (usuarioCorreo != null) {
                                                repo.eliminarAlimento(alimento.id, usuarioCorreo)
                                                listaAlimentos.remove(alimento) // Actualización visual inmediata
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FormularioAlimento(
    alimentoInicial: Alimento? = null, // Puede recibir un alimento para editar
    onGuardar: (Alimento) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    // Si estamos editando, los campos se rellenan con los datos existentes. Si no, quedan vacíos.
    var nombre by remember { mutableStateOf(alimentoInicial?.nombre ?: "") }
    var cantidad by remember { mutableStateOf(alimentoInicial?.cantidad ?: "") }
    var fechaIngreso by remember { mutableStateOf(alimentoInicial?.fechaIngreso ?: "") }
    var fechaVencimiento by remember { mutableStateOf(alimentoInicial?.fechaVencimiento ?: "") }
    var tipo by remember { mutableStateOf(alimentoInicial?.tipo ?: "") }

    LazyColumn(Modifier.fillMaxWidth().padding(16.dp)) {
        item {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre del alimento") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(cantidad, { cantidad = it }, label = { Text("Cantidad (Kg, L, etc.)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            FechaConCalendario("Fecha de ingreso", fechaIngreso, { fechaIngreso = it }, context)
            Spacer(Modifier.height(8.dp))
            FechaConCalendario("Fecha de vencimiento", fechaVencimiento, { fechaVencimiento = it }, context)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(tipo, { tipo = it }, label = { Text("Tipo de alimento") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (nombre.isNotBlank()) {
                            onGuardar(
                                Alimento(
                                    id = alimentoInicial?.id ?: 0, // Mantenemos el ID si estamos editando
                                    nombre = nombre,
                                    cantidad = cantidad,
                                    fechaIngreso = fechaIngreso,
                                    fechaVencimiento = fechaVencimiento,
                                    tipo = tipo
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8622))
                ) { Text("Guardar") }
                OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
            }
        }
    }
}



    // === COMPONENTE DE CALENDARIO (con ícono funcional) ===
    @Composable
    fun FechaConCalendario(
        label: String,
        fecha: String,
        onFechaSeleccionada: (String) -> Unit,
        context: Context
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Abrir calendario",
                    tint = Color(0xFF524E58),
                    modifier = Modifier.clickable {
                        DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                onFechaSeleccionada("$selectedDay/${selectedMonth + 1}/$selectedYear")
                            },
                            year, month, day
                        ).show()
                    }
                )

            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            onFechaSeleccionada("$selectedDay/${selectedMonth + 1}/$selectedYear")
                        },
                        year, month, day
                    ).show()
                }
        )
    }

