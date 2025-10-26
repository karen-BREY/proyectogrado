package com.proyecto_grado


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "NutriBovino.db"
    }

    override fun onCreate(db: SQLiteDatabase) {

        // === USUARIO ===
        db.execSQL("""
            CREATE TABLE Usuario (
                idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                telefono TEXT,
                contrasena TEXT NOT NULL
            )
        """)

        // === LOTE ===
        db.execSQL("""
            CREATE TABLE Lote (
                idLote INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER NOT NULL,
                observacion TEXT,
                usuario_correo TEXT not null
            )
        """)

        // === ANIMAL ===
        db.execSQL("""
            CREATE TABLE Animal (
                idAnimal INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                numero INTEGER NOT NULL,
                lote INTEGER,
                fechaNacimiento TEXT,
                edad TEXT, -- Considera cambiar a INTEGER si solo guardas números
                raza TEXT,
                pesoActual REAL,
                observacion TEXT,
                usuario_correo TEXT NOT NULL, 
                FOREIGN KEY (lote) REFERENCES Lote(idLote)
            )
        """)

        // === ALIMENTOS ===
        db.execSQL("""
            CREATE TABLE Alimentos (
                idAlimento INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cantidad REAL NOT NULL,
                fechaIngreso TEXT,
                fechaVencimiento TEXT,
                tipoAlimento TEXT,
                usuario_correo TEXT NOT NULL 
            )
        """)

        // === ALIMENTACION ===
        db.execSQL("""
            CREATE TABLE Alimentacion (
                idAlimentacion INTEGER PRIMARY KEY AUTOINCREMENT,
                numeroAnimal INTEGER NOT NULL,
                lote INTEGER,
                alimento INTEGER,
                cantidad REAL,
                frecuencia TEXT,
                observacion TEXT,
                usuario_correo TEXT NOT NULL,  
                FOREIGN KEY (numeroAnimal) REFERENCES Animal(idAnimal),
                FOREIGN KEY (lote) REFERENCES Lote(idLote),
                FOREIGN KEY (alimento) REFERENCES Alimentos(idAlimento)
            )
        """)

        // === POTRERO ===
        db.execSQL("""
            CREATE TABLE Potrero (
                idPotrero INTEGER PRIMARY KEY AUTOINCREMENT,
                numeroPotrero INTEGER NOT NULL,
                tipoPasto TEXT,
                lote INTEGER,
                fechaIngreso TEXT,
                fechaSalida TEXT,
                observacion TEXT,
                usuario_correo TEXT NOT NULL, 
                FOREIGN KEY (lote) REFERENCES Lote(idLote)
            )
        """)

    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Usuario")
        db?.execSQL("DROP TABLE IF EXISTS Lote")
        db?.execSQL("DROP TABLE IF EXISTS Animal")
        db?.execSQL("DROP TABLE IF EXISTS Alimentos")
        db?.execSQL("DROP TABLE IF EXISTS Alimentacion")
        db?.execSQL("DROP TABLE IF EXISTS Potrero")


        onCreate(db!!)
    }

    // Devuelve lista de pares (id, nombre) de animales
    fun obtenerAnimalesConId(usuarioCorreo: String): List<Pair<Int, String>> {
        val lista = mutableListOf<Pair<Int, String>>()
        val db = readableDatabase
        // ✅ Se añade el filtro WHERE
        val cursor = db.rawQuery("SELECT idAnimal, nombre FROM Animal WHERE usuario_correo = ?", arrayOf(usuarioCorreo))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow("idAnimal"))
                    val nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                    lista.add(Pair(id, nombre))
                } while (it.moveToNext())
            }
        }
        return lista
    }

    // Devuelve lista de pares (id, nombre) de lotes
    fun obtenerLotesConId(usuarioCorreo: String): List<Pair<Int, String>> {
        val lista = mutableListOf<Pair<Int, String>>()
        val db = readableDatabase
        // ✅ Se añade el filtro WHERE
        val cursor = db.rawQuery("SELECT idLote, numero FROM Lote WHERE usuario_correo = ?", arrayOf(usuarioCorreo))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow("idLote"))
                    val numero = it.getInt(it.getColumnIndexOrThrow("numero")).toString()
                    lista.add(Pair(id, numero))
                } while (it.moveToNext())
            }
        }
        return lista
    }

    // Devuelve lista de pares (id, nombre) de alimentos
    fun obtenerAlimentosConId(usuarioCorreo: String): List<Pair<Int, String>> {
        val lista = mutableListOf<Pair<Int, String>>()
        val db = readableDatabase
        // ✅ Se añade el filtro WHERE
        val cursor = db.rawQuery("SELECT idAlimento, nombre FROM Alimentos WHERE usuario_correo = ?", arrayOf(usuarioCorreo))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow("idAlimento"))
                    val nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                    lista.add(Pair(id, nombre))
                } while (it.moveToNext())
            }
        }
        return lista
    }

    // Funciones para convertir ID a nombre (para mostrar en historial)
    fun obtenerNombreAnimal(id: Int, usuarioCorreo: String): String {
        val db = readableDatabase
        var nombre = ""
        val cursor = db.rawQuery("SELECT nombre FROM Animal WHERE idAnimal = ? AND usuario_correo = ?", arrayOf(id.toString(), usuarioCorreo))
        cursor.use {
            if (it.moveToFirst()) {
                nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
            }
        }
        return nombre
    }

    fun obtenerNumeroLote(id: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT numero FROM Lote WHERE idLote = ?", arrayOf(id.toString()))
        val numero = if (cursor.moveToFirst()) cursor.getInt(0).toString() else "Desconocido"
        cursor.close()
        return numero
    }

    fun obtenerNombreAlimento(id: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Alimentos WHERE idAlimento = ?", arrayOf(id.toString()))
        val nombre = if (cursor.moveToFirst()) cursor.getString(0) else "Desconocido"
        cursor.close()
        return nombre
    }


    fun obtenerAlimentoPorVencer(): List<Alimento> {
        val lista = mutableListOf<Alimento>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Alimentos", null)

        if (cursor.moveToFirst()) {
            do {
                val fechaVencimientoStr = cursor.getString(cursor.getColumnIndexOrThrow("fechaVencimiento"))

                // Se utiliza el validador externo para mantener la lógica limpia aquí.
                if (DateValidator.isWithinAlertRange(fechaVencimientoStr)) {
                    lista.add(
                        Alimento(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow("idAlimento")),
                            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            // Nota: La cantidad es REAL en la BD, es mejor leerla como tal.
                            cantidad = cursor.getFloat(cursor.getColumnIndexOrThrow("cantidad")).toString(),
                            fechaIngreso = cursor.getString(cursor.getColumnIndexOrThrow("fechaIngreso")),
                            fechaVencimiento = fechaVencimientoStr,
                            // Asumiendo que 'tipo' es el nombre correcto, si no, ajústalo a 'tipoAlimento'
                            tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimento"))
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

}




