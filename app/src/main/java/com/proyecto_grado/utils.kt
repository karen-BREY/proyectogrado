package com.proyecto_grado.ui.theme.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ReportUtils {

    fun exportReportToWhatsApp(context: Context, reportText: String) {
        try {
            // Crear archivo temporal
            val file = File(context.cacheDir, "reporte.txt")
            FileOutputStream(file).use {
                it.write(reportText.toByteArray())
            }

            // Obtener URI segura del archivo
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

            // Crear intent para compartir
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Enviar a WhatsApp
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
