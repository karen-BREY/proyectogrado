package com.proyecto_grado


import java.text.SimpleDateFormat
import java.util.*

object DateValidator {

    // Extraemos la lógica pura aquí. No depende de Android.
    fun isWithinAlertRange(fechaVencimientoStr: String, dateFormat: String = "dd/MM/yyyy"): Boolean {
        return try {
            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

            // Crea la fecha de hoy, pero a medianoche (para evitar problemas con la hora).
            val calendarHoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val fechaHoy = calendarHoy.time

            // Crea la fecha límite (hoy + 3 días).
            val calendarLimite = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 3)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
            val fechaLimite = calendarLimite.time

            // Convierte la fecha del alimento.
            val fechaVencimientoDate = sdf.parse(fechaVencimientoStr) ?: return false

            // La condición clave: la fecha está en el futuro, pero antes del límite.
            fechaVencimientoDate.after(fechaHoy) && fechaVencimientoDate.before(fechaLimite)

        } catch (e: Exception) {
            false
        }
    }
}
