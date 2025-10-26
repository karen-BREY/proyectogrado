package com.proyecto_grado.Perfil // Asegúrate de que el paquete sea el correcto

import android.content.Context
import com.proyecto_grado.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Esta es la "plantilla" de cómo se ve un Usuario en nuestro código.
data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val telefono: String
)

class  PerfilRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Función para buscar un usuario por su correo electrónico.
    // Es "suspend" porque la consulta a la base de datos puede tardar un poco.
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        // Usamos withContext(Dispatchers.IO) para no bloquear la interfaz de usuario.
        return withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            var usuario: Usuario? = null

            val cursor = db.query(
                "Usuario", // 1. Nombre de la tabla
                arrayOf("idUsuario", "nombre", "apellido", "correo", "telefono"), // 2. Columnas que queremos
                "correo = ?", // 3. La condición de búsqueda
                arrayOf(correo), // 4. El valor para la condición (el email del usuario actual)
                null, null, null
            )

            cursor?.use { // .use se encarga de cerrar el cursor automáticamente
                if (it.moveToFirst()) {
                    usuario = Usuario(
                        idUsuario = it.getInt(it.getColumnIndexOrThrow("idUsuario")),
                        nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                        apellido = it.getString(it.getColumnIndexOrThrow("apellido")),
                        correo = it.getString(it.getColumnIndexOrThrow("correo")),
                        telefono = it.getString(it.getColumnIndexOrThrow("telefono"))
                    )
                }
            }
            // db.close() // dbHelper se encarga de esto, no es necesario cerrarlo aquí.
            usuario // Devuelve el usuario encontrado o null si no se encontró
        }
    }
}
