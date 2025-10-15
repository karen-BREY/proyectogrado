package com.proyecto_grado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.proyecto_grado.ui.theme.Proyecto_gradoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.proyecto_grado.Navegacion.kt.Navegacion


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        enableEdgeToEdge()
        setContent {
            Proyecto_gradoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navegacion(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

