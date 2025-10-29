package com.proyecto_grado.Registar.kt

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color
import android.content.ContentValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.proyecto_grado.DatabaseHelper








@Composable
fun Registrar(
    modifier: Modifier = Modifier,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbHelper = remember { DatabaseHelper(context) }

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") })
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                    telefono = it
                }
            },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (contrasenaVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (contrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmarContrasenaVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (confirmarContrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || apellido.isBlank() || telefono.isBlank() ||
                    correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()
                ) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!correo.contains("@") || !correo.contains(".")) {
                    Toast.makeText(context, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (contrasena != confirmarContrasena) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (contrasena.length < 6) {
                    Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            // 🔹 Insertar datos en la base SQLite local
                            try {
                                val db = dbHelper.writableDatabase
                                val values = ContentValues().apply {
                                    put("nombre", nombre)
                                    put("apellido", apellido)
                                    put("correo", correo)
                                    put("telefono", telefono)
                                    put("contrasena", contrasena)
                                }
                                db.insert("Usuario", null, values)
                                db.close()

                                Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_LONG).show()
                                onBackToLogin()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al guardar en base local: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                        } else {
                            Toast.makeText(
                                context,
                                "Error al registrar: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E8622)
            )
        ) {
            Text("Registrar")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrarPreview() {
    Registrar(onBackToLogin = {})
}
