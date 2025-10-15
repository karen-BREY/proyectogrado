package com.proyecto_grado

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // === USUARIO ===
        db.execSQL("""
            CREATE TABLE Usuario (
                idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                telefono TEXT NOT NULL, --modificar
                correo TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL
            );
        """)

        // === LOTE ===
        db.execSQL("""
            CREATE TABLE Lote (
                idLote INTEGER PRIMARY KEY AUTOINCREMENT,
                numero NUM NOT NULL, --modificar
                observaciones TEXT,
                idUsuario INTEGER,
                FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
            );
        """)

        // === ANIMAL ===
        db.execSQL("""
            CREATE TABLE Animal (
                idAnimal INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                numero TEXT NOT NULL, --modificar
                raza TEXT,
                fechaNacimientoIngreso TEXT,
                pesoActual REAL,
                edad TEXT NOT NULL, --modificar
                observaciones TEXT,
                idUsuario INTEGER,
                idLote INTEGER,
                FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                FOREIGN KEY (idLote) REFERENCES Lote(idLote)
            );
        """)

        // === POTRERO ===
        db.execSQL("""
            CREATE TABLE Potrero ( 
                idPotrero INTEGER PRIMARY KEY AUTOINCREMENT,
                numero TEXT NOT NULL, --modificar
                tipopasto REAL,
                lote TEXT,
                fechaingreso TEXT NOT NULL,
                fechasalida TEXT NOT NULL,
                observacion TEXT NOT NULL
                idLote INTEGER,
                FOREIGN KEY (idLote) REFERENCES Lote(idLote)
            );
        """)

        // === TIPO ALIMENTO ===
        db.execSQL("""
            CREATE TABLE TipoAlimento (
                idTipoAlimento INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT
            );
        """)

        // === ALIMENTO ===
        db.execSQL("""
            CREATE TABLE Alimento (
                idAlimento INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,  --modificar
                cantidad REAL,
                fechaingreso TEXT NOT NULL,
                fechaVencimiento TEXT,
                idTipoAlimento INTEGER,
                FOREIGN KEY (idTipoAlimento) REFERENCES TipoAlimento(idTipoAlimento)
            );
        """)

        // === ALIMENTACIÓN ===
        db.execSQL("""
            CREATE TABLE Alimentacion (
                idAlimentacion INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                cantidad REAL,
                frecuencia TEXT,
                observaciones TEXT,
                idAnimal INTEGER,
                idLote INTEGER,
                idAlimento INTEGER,
                FOREIGN KEY (idAnimal) REFERENCES Animal(idAnimal),
                FOREIGN KEY (idLote) REFERENCES Lote(idLote),
                FOREIGN KEY (idAlimento) REFERENCES Alimento(idAlimento)
            );
        """)

        // --- reporte



        // === TIPO RECORDATORIO ===
        db.execSQL("""
            CREATE TABLE TipoRecordatorio (
                idTipoRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT
            );
        """)

        // === RECORDATORIO ===
        db.execSQL("""
            CREATE TABLE Recordatorio (
                idRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                estado TEXT,
                idUsuario INTEGER,
                idTipoRecordatorio INTEGER,
                FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                FOREIGN KEY (idTipoRecordatorio) REFERENCES TipoRecordatorio(idTipoRecordatorio)
            );
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Recordatorio")
        db.execSQL("DROP TABLE IF EXISTS TipoRecordatorio")
        db.execSQL("DROP TABLE IF EXISTS ReporteAlimento")
        db.execSQL("DROP TABLE IF EXISTS ReportePeso")
        db.execSQL("DROP TABLE IF EXISTS Alimentacion")
        db.execSQL("DROP TABLE IF EXISTS Alimento")
        db.execSQL("DROP TABLE IF EXISTS TipoAlimento")
        db.execSQL("DROP TABLE IF EXISTS Potrero")
        db.execSQL("DROP TABLE IF EXISTS Animal")
        db.execSQL("DROP TABLE IF EXISTS Lote")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "NutriBovino.db"
        private const val DATABASE_VERSION = 1
    }
}

