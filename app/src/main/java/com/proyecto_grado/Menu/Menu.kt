package com.proyecto_grado.Menu.kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.proyecto_grado.Navegacion.kt.Navegacion


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navegacion() // ✅ aquí va el NavHost
            }
        }
    }
}

@Composable
fun MenuScreen(
    onRegistrarAnimal: () -> Unit = {},
    onRegistrarLote: () -> Unit = {},
    onRegistrarAlimento: () -> Unit = {},
    onAlimentacion: () -> Unit = {},
    onPotrero: () -> Unit = {},
    onReportes: () -> Unit = {},
    onPerfilUsuario: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        // Encabezado con icono de usuario y texto Bienvenido
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Usuario",
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onPerfilUsuario() } //  al hacer clic abre perfil
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Opciones del menú
        MenuItem(Icons.Default.Pets, "Registrar Animal", onClick = onRegistrarAnimal)
        MenuItem(Icons.Default.Home, "Registrar Lote", onClick = onRegistrarLote)
        MenuItem(Icons.Default.FoodBank, "Registrar Alimento", onClick = onRegistrarAlimento)
        MenuItem(Icons.Default.EditNote, "Alimentación", onClick = onAlimentacion)
        MenuItem(Icons.Default.Grass, "Potrero", onClick = onPotrero)
        MenuItem(Icons.Default.BarChart, "Reportes", onClick = onReportes)
    }
}


@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = text,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen()
}