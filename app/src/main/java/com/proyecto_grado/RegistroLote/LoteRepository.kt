package com.proyecto_grado

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase


class LoteRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // --- Insertar un nuevo lote ---
    fun insertarLote(lote: Lote, usuarioCorreo: String): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", lote.numero)
            put("observacion", lote.observacion)
            put("usuario_correo", usuarioCorreo) // ✅ Se añade el correo del usuario
        }
        // Usamos 'use' para que la base de datos se cierre automáticamente.
        db.use {
            return it.insert("Lote", null, values)
        }
    }

    // --- Obtener todos los lotes ---
    fun obtenerLotes(usuarioCorreo: String): List<Lote> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Lote>()

        // ✅ Se añade el filtro "WHERE usuario_correo = ?"
        val cursor = db.rawQuery(
            "SELECT idLote, numero, observacion FROM Lote WHERE usuario_correo = ?",
            arrayOf(usuarioCorreo)
        )

        // Usamos 'use' para que el cursor se cierre automáticamente.
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val lote = Lote(
                        idLote = it.getInt(it.getColumnIndexOrThrow("idLote")),
                        numero = it.getInt(it.getColumnIndexOrThrow("numero")),
                        observacion = it.getString(it.getColumnIndexOrThrow("observacion"))
                    )
                    lista.add(lote)
                } while (it.moveToNext())
            }
        }
        db.close() // Cerramos la conexión a la base de datos.
        return lista
    }

    // --- Actualizar lote ---
    fun actualizarLote(lote: Lote, usuarioCorreo: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", lote.numero)
            put("observacion", lote.observacion)
        }
        // ✅ Se añade "AND usuario_correo = ?" a la cláusula WHERE.
        db.use {
            return it.update(
                "Lote",
                values,
                "idLote = ? AND usuario_correo = ?",
                arrayOf(lote.idLote.toString(), usuarioCorreo)
            )
        }
    }

    // ✅ CORREGIDO: Acepta 'usuarioCorreo' para eliminar de forma segura.
    fun eliminarLote(idLote: Int, usuarioCorreo: String): Int {
        val db = dbHelper.writableDatabase
        // ✅ Se añade "AND usuario_correo = ?" a la cláusula WHERE.
        db.use {
            return it.delete(
                "Lote",
                "idLote = ? AND usuario_correo = ?",
                arrayOf(idLote.toString(), usuarioCorreo)
            )
        }
    }
}
