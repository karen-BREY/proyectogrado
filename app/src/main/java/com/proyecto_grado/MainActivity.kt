package com.proyecto_grado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.proyecto_grado.Navegacion.kt.Navegacion
import com.proyecto_grado.login.kt.LoginScreen
import com.proyecto_grado.ui.theme.Proyecto_gradoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

