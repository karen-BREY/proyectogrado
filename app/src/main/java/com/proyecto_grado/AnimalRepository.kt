package com.proyecto_grado

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class AnimalRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // --- Insertar un nuevo animal ---
    fun insertarAnimal(animal: Animal, usuarioCorreo: String): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", animal.nombre)
            put("numero", animal.numero)
            put("lote", animal.lote)
            put("fechaNacimiento", animal.fechaNacimiento)
            put("edad", animal.edad)
            put("raza", animal.raza)
            put("pesoActual", animal.pesoActual)
            put("observacion", animal.observaciones)
            put("usuario_correo", usuarioCorreo) // ✅ Se añade el correo del usuario
        }
        val result = db.insert("Animal", null, values)
        db.close()
        return result
    }

    // --- Obtener todos los animales registrados ---
    // En AnimalRepository.kt

    fun obtenerAnimales(usuarioCorreo: String): List<Animal> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Animal>()

        // ✅ Se añade el filtro "WHERE usuario_correo = ?"
        val cursor = db.rawQuery(
            "SELECT idAnimal, nombre, numero, lote, fechaNacimiento, edad, raza, pesoActual, observacion FROM Animal WHERE usuario_correo = ?",
            arrayOf(usuarioCorreo)
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val animal = Animal(
                        id = it.getIntOrNull(0) ?: 0,
                        nombre = it.getStringOrNull(1) ?: "",
                        numero = it.getIntOrNull(2) ?: 0,
                        lote = it.getIntOrNull(3) ?: 0,
                        fechaNacimiento = it.getStringOrNull(4) ?: "",
                        edad = it.getStringOrNull(5) ?: "",
                        raza = it.getStringOrNull(6) ?: "",
                        pesoActual = it.getDoubleOrNull(7) ?: 0.0,
                        observaciones = it.getStringOrNull(8) ?: ""
                    )
                    lista.add(animal)
                } while (it.moveToNext())
            }
        }
        db.close()
        return lista
    }


    fun actualizarAnimal(animal: Animal, usuarioCorreo: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", animal.nombre)
            put("numero", animal.numero) // Permitimos actualizar el número también
            put("lote", animal.lote)
            put("fechaNacimiento", animal.fechaNacimiento)
            put("edad", animal.edad)
            put("raza", animal.raza)
            put("pesoActual", animal.pesoActual)
            put("observacion", animal.observaciones)
        }
        // ✅ Se añade "AND usuario_correo = ?" para seguridad. Se actualiza por 'idAnimal'.
        val result = db.update(
            "Animal",
            values,
            "idAnimal = ? AND usuario_correo = ?",
            arrayOf(animal.id.toString(), usuarioCorreo)
        )
        db.close()
        return result > 0
    }

    // --- Eliminar un animal ---
    fun eliminarAnimal(idAnimal: Int, usuarioCorreo: String): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            "Animal",
            "idAnimal = ? AND usuario_correo = ?",
            arrayOf(idAnimal.toString(), usuarioCorreo)
        )
        db.close()
        return result > 0
    }

    // Extensiones para evitar crash con null
    private fun android.database.Cursor.getStringOrNull(index: Int): String? =
        if (isNull(index)) null else getString(index)

    private fun android.database.Cursor.getIntOrNull(index: Int): Int? =
        if (isNull(index)) null else getInt(index)

    private fun android.database.Cursor.getDoubleOrNull(index: Int): Double? =
        if (isNull(index)) null else getDouble(index)
}


