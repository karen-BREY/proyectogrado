package com.proyecto_grado

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.proyecto_grado.Alimentacion.Alimentacion
import java.io.File
import com.google.firebase.database.FirebaseDatabase

private val firebaseDB = FirebaseDatabase.getInstance().reference



class AlimentacionRepository(private val context: Context?) {

    private val dbHelper = context?.let { DatabaseHelper(it) }


    fun obtenerAnimales(usuarioCorreo: String): List<Pair<Int, String>> {
        val lista = mutableListOf<Pair<Int, String>>()
        try {
            val db = dbHelper?.readableDatabase ?: return lista
            val cursor = db.rawQuery(
                "SELECT idAnimal, nombre FROM Animal WHERE usuario_correo = ?",
                arrayOf(usuarioCorreo)
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        lista.add(Pair(it.getInt(0), it.getString(1)))
                    } while (it.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error obteniendo animales", e)
        }
        return lista
    }

    fun insertarAlimentacion(alimentacion: Alimentacion, usuarioCorreo: String) {
        try {
            dbHelper?.writableDatabase?.use { db ->
                val valores = ContentValues().apply {
                    put("numeroAnimal", alimentacion.numeroAnimal)
                    put("lote", alimentacion.lote)
                    put("alimento", alimentacion.alimento)
                    put("cantidad", alimentacion.cantidad)
                    put("frecuencia", alimentacion.frecuencia)
                    put("observacion", alimentacion.observacion)
                    put("usuario_correo", usuarioCorreo)
                }

                val resultado = db.insert("Alimentacion", null, valores)
                if (resultado != -1L) {
                    val idAlimento = alimentacion.alimento
                    val cantidadUsada = alimentacion.cantidad
                    AlimentoRepository(context).descontarCantidadAlimento(idAlimento, cantidadUsada, usuarioCorreo)
                }
            }
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error insertando alimentación", e)
        }
    }

    fun obtenerAlimentaciones(usuarioCorreo: String): List<Alimentacion> {
        val lista = mutableListOf<Alimentacion>()
        try {
            val db = dbHelper?.readableDatabase ?: return lista
            val cursor = db.rawQuery(
                "SELECT idAlimentacion, numeroAnimal, lote, alimento, cantidad, frecuencia, observacion FROM Alimentacion WHERE usuario_correo = ?",
                arrayOf(usuarioCorreo)
            )
            cursor.use { c ->
                if (c.moveToFirst()) {
                    do {
                        lista.add(
                            Alimentacion(
                                idAlimentacion = c.getInt(0),
                                numeroAnimal = c.getInt(1),
                                lote = c.getInt(2),
                                alimento = c.getInt(3),
                                cantidad = c.getDouble(4),
                                frecuencia = c.getString(5),
                                observacion = c.getString(6)
                            )
                        )
                    } while (c.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error leyendo alimentaciones", e)
        }
        return lista
    }

    fun eliminarAlimentacion(id: Int, usuarioCorreo: String): Boolean {
        return try {
            dbHelper?.writableDatabase?.use { db ->
                val filas = db.delete(
                    "Alimentacion",
                    "idAlimentacion = ? AND usuario_correo = ?",
                    arrayOf(id.toString(), usuarioCorreo)
                )
                filas > 0
            } ?: false
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error eliminando alimentación", e)
            false
        }
    }


    // ✅ CORREGIDO: Ahora pide 'usuarioCorreo' para filtrar.
    fun obtenerReportePorAnimalId(animalId: Int, usuarioCorreo: String): ReporteGeneral? {
        val db = dbHelper?.readableDatabase ?: return null
        var reporte: ReporteGeneral? = null

        val sqlQuery = """
            SELECT a.nombre, a.raza, a.fechaNacimiento, a.pesoActual,
                   COALESCE(al.alimento, 'Sin registro') AS alimento,
                   COALESCE(al.observacion, '') AS observacion
            FROM Animal a
            LEFT JOIN Alimentacion al ON a.idAnimal = al.numeroAnimal AND al.usuario_correo = ?
            WHERE a.idAnimal = ? AND a.usuario_correo = ?
            ORDER BY al.idAlimentacion DESC
            LIMIT 1
        """.trimIndent()

        try {
            // ✅ CORREGIDO: Pasamos el correo del usuario 3 veces a la consulta.
            db.rawQuery(sqlQuery, arrayOf(usuarioCorreo, animalId.toString(), usuarioCorreo)).use { cursor ->
                if (cursor.moveToFirst()) {
                    reporte = ReporteGeneral(
                        nombreAnimal = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        raza = cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                        fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento")),
                        pesoActual = cursor.getDouble(cursor.getColumnIndexOrThrow("pesoActual")),
                        alimento = cursor.getString(cursor.getColumnIndexOrThrow("alimento")),
                        observacion = cursor.getString(cursor.getColumnIndexOrThrow("observacion"))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error obteniendo reporte por ID", e)
        }
        return reporte
    }

    //  CORREGIDO: Ahora pide 'usuarioCorreo' para filtrar.
    fun obtenerReporteGeneral(usuarioCorreo: String): List<ReporteGeneral> {
        val lista = mutableListOf<ReporteGeneral>()
        val db = dbHelper?.readableDatabase ?: return lista

        try {
            db.rawQuery(
                """
                SELECT a.nombre, a.raza, a.fechaNacimiento, a.pesoActual, 
                       COALESCE(al.alimento, 'Sin registro') AS alimento,
                       COALESCE(al.observacion, '') AS observacion
                FROM Animal a
                LEFT JOIN Alimentacion al ON al.numeroAnimal = a.idAnimal AND al.usuario_correo = ?
                WHERE a.usuario_correo = ?
                ORDER BY a.nombre
                """.trimIndent(),
                arrayOf(usuarioCorreo, usuarioCorreo)
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        lista.add(
                            ReporteGeneral(
                                nombreAnimal = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                                raza = cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento")),
                                pesoActual = cursor.getDouble(cursor.getColumnIndexOrThrow("pesoActual")),
                                alimento = cursor.getString(cursor.getColumnIndexOrThrow("alimento")),
                                observacion = cursor.getString(cursor.getColumnIndexOrThrow("observacion"))
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e("ReporteRepo", "Error obteniendo reporte general", e)
        }
        return lista
    }

    fun crearYGuardarReporteCSV(context: Context, reporte: ReporteGeneral): Uri? {
        val csvHeader = "Concepto,Valor\n"
        val csvData = """
        Animal,${reporte.nombreAnimal}
        Raza,${reporte.raza}
        Fecha de Nacimiento,${reporte.fechaNacimiento}
        Peso Actual (kg),${reporte.pesoActual}
        Último Alimento Registrado,${reporte.alimento}
        Observación,${reporte.observacion.replace("\n", " ")}
    """.trimIndent()

        val csvContent = csvHeader + csvData

        try {
            // Usaremos el directorio de caché, que no requiere permisos de escritura especiales
            val cachePath = File(context.cacheDir, "reports/")
            cachePath.mkdirs() // Crea el directorio si no existe

            val fileName = "reporte_${
                reporte.nombreAnimal.replace(
                    " ",
                    "_"
                )
            }_${System.currentTimeMillis()}.csv"
            val file = File(cachePath, fileName)
            file.writeText(csvContent, Charsets.UTF_8)

            // Necesitamos un FileProvider para compartir el archivo de forma segura
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            Log.e("AlimentacionRepo", "Error al crear archivo CSV", e)
            return null
        }
    }

    // 3. FUNCIÓN PARA COMPARTIR POR WHATSAPP
    fun compartirReportePorWhatsApp(context: Context, fileUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_TEXT, "Adjunto el reporte del animal.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            `package` = "com.whatsapp"
        }

        try {
            context.startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show()
            // Opcional: Abrir un selector general de apps para compartir
            val generalIntent = Intent.createChooser(shareIntent, "Compartir reporte")
            context.startActivity(generalIntent)
        }
    }
}




