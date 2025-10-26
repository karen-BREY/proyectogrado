package com.proyecto_grado

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class AlimentoRepository(context: Context?) {
    private val dbHelper = DatabaseHelper(context)



    // === Insertar un nuevo alimento ===
    fun insertarAlimento(alimento: Alimento, usuarioCorreo: String): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", alimento.nombre)
            put("cantidad", alimento.cantidad.toDoubleOrNull())
            put("fechaIngreso", alimento.fechaIngreso)
            put("fechaVencimiento", alimento.fechaVencimiento)
            put("tipoAlimento", alimento.tipo)
            put("usuario_correo", usuarioCorreo)
        }
        val result = db.insert("Alimentos", null, values)
        db.close()
        return result
    }

    // === Obtener todos los alimentos ===
    fun obtenerAlimentos(usuarioCorreo: String): List<Alimento> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Alimento>()
        val cursor = db.rawQuery(
            "SELECT idAlimento, nombre, cantidad, fechaIngreso, fechaVencimiento, tipoAlimento FROM Alimentos WHERE usuario_correo = ?",
            arrayOf(usuarioCorreo) // ✅ CORREGIDO: Pasamos el correo del usuario a la consulta.
        )


        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Alimento(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        cantidad = cursor.getDouble(2).toString(),
                        fechaIngreso = cursor.getString(3),
                        fechaVencimiento = cursor.getString(4),
                        tipo = cursor.getString(5)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    // === Eliminar un alimento ===
    fun eliminarAlimento(id: Int, usuarioCorreo: String): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            "Alimentos",
            "idAlimento = ? AND usuario_correo = ?", // Cláusula WHERE con doble condición
            arrayOf(id.toString(), usuarioCorreo)
        )
        db.close()
        return result
    }

    // === Actualizar alimento ===
    fun actualizarAlimento(alimento: Alimento, usuarioCorreo: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", alimento.nombre)
            put("cantidad", alimento.cantidad.toDoubleOrNull())
            put("fechaIngreso", alimento.fechaIngreso)
            put("fechaVencimiento", alimento.fechaVencimiento)
            put("tipoAlimento", alimento.tipo)
            // No es necesario actualizar el correo del usuario
        }
        val result = db.update(
            "Alimentos",
            values,
            "idAlimento = ? AND usuario_correo = ?", // Cláusula WHERE con doble condición
            arrayOf(alimento.id.toString(), usuarioCorreo)
        )
        db.close()
        return result
    }

    fun descontarCantidadAlimento(idAlimento: Int, cantidadUsada: Double, usuarioCorreo: String): Boolean {
        return try {
            dbHelper.writableDatabase.use { db ->
                val cursor = db.rawQuery(
                    "SELECT cantidad FROM Alimentos WHERE idAlimento = ? AND usuario_correo = ?",
                    arrayOf(idAlimento.toString(), usuarioCorreo)
                )
                if (cursor.moveToFirst()) {
                    val cantidadActual = cursor.getDouble(0)
                    val nuevaCantidad = (cantidadActual - cantidadUsada).coerceAtLeast(0.0)
                    val values = ContentValues().apply {
                        put("cantidad", nuevaCantidad)
                    }
                    val filas = db.update(
                        "Alimentos",
                        values,
                        "idAlimento = ? AND usuario_correo = ?",
                        arrayOf(idAlimento.toString(), usuarioCorreo)
                    )
                    cursor.close()
                    return filas > 0
                }
                cursor.close()
                false
            }
        } catch (e: Exception) {
            Log.e("AlimentoRepo", "Error descontando cantidad", e)
            false
        }
    }

    fun obtenerAlimentosPorVencer(usuarioCorreo: String): List<Alimento> {
        val lista = mutableListOf<Alimento>()
        dbHelper.readableDatabase.use { db ->
            val cursor = db.rawQuery(
                "SELECT idAlimento, nombre, cantidad, fechaIngreso, fechaVencimiento, tipoAlimento FROM Alimentos WHERE usuario_correo = ?",
                arrayOf(usuarioCorreo)
            )
            cursor.use { c ->
                if (c.moveToFirst()) {
                    do {
                        val fechaVencStr = c.getString(4)
                        if (DateValidator.isWithinAlertRange(fechaVencStr)) {
                            lista.add(
                                Alimento(
                                    id = c.getInt(0),
                                    nombre = c.getString(1),
                                    cantidad = c.getDouble(2).toString(),
                                    fechaIngreso = c.getString(3),
                                    fechaVencimiento = fechaVencStr,
                                    tipo = c.getString(5)
                                )
                            )
                        }
                    } while (c.moveToNext())
                }
            }
        }
        return lista
    }
}

