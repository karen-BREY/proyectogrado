package com.proyecto_grado.Potrero

import android.content.ContentValues
import com.proyecto_grado.DatabaseHelper
import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PotreroRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertarPotrero(potrero: Potrero, usuarioCorreo: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("numero", potrero.numero)
            put("tipoPasto", potrero.tipoPasto)
            put("lote", potrero.lote)  // Puede ser null
            put("fechaIngreso", potrero.fechaIngreso)
            put("fechaSalida", potrero.fechaSalida)
            put("observacion", potrero.observacion)
            put("usuario_correo", usuarioCorreo)
        }
        db.insert("Potrero", null, values)
        db.close()
    }

    fun diasRestantes(fechaSalida: String?): Long {
        if (fechaSalida.isNullOrEmpty()) return -1
        return try {
            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val salida = LocalDate.parse(fechaSalida, formato)
            val hoy = LocalDate.now()
            ChronoUnit.DAYS.between(hoy, salida)
        } catch (e: Exception) {
            -1
        }
    }

    fun obtenerPotreros(usuarioCorreo: String): List<Potrero> {
        val lista = mutableListOf<Potrero>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT idPotrero, numero, tipoPasto, lote, fechaIngreso, fechaSalida, observacion 
            FROM Potrero 
            WHERE usuario_correo = ?
            """.trimIndent(),
            arrayOf(usuarioCorreo)
        )

        if (cursor.moveToFirst()) {
            do {
                val potrero = Potrero(
                    idPotrero = cursor.getInt(0),
                    numero = cursor.getInt(1),
                    tipoPasto = cursor.getString(2) ?: "",
                    lote = if (!cursor.isNull(3)) cursor.getInt(3) else null, // 🔹 Evita crash si lote es NULL
                    fechaIngreso = cursor.getString(4) ?: "",
                    fechaSalida = cursor.getString(5) ?: "",
                    observacion = cursor.getString(6) ?: ""
                )
                lista.add(potrero)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close() // 🔹 Cierra conexión al terminar
        return lista
    }

    fun eliminarPotrero(idPotrero: Int, usuarioCorreo: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            "Potrero",
            "idPotrero = ? AND usuario_correo = ?",
            arrayOf(idPotrero.toString(), usuarioCorreo)
        )
        db.close() // 🔹 Cierra conexión tras eliminar
    }
}

