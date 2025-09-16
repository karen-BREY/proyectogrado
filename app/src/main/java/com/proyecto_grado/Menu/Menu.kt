package com.proyecto_grado.Menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainMenuScreen(
                            onMenuClick = { id ->
                                // placeholder: manejar navegación según id
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MainMenuScreen(onMenuClick: (String) -> Unit) {
        val spacing = 16.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing, vertical = 20.dp)
        ) {
            // Header: icon + Bienvenido
            Row(verticalAlignment = Alignment.CenterVertically) {
                // user icon
                Surface(
                    shape = CircleShape,
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Usuario",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pie chart card
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pie chart (custom Canvas)
                    PieChart(
                        segments = listOf(
                            0.65f to MaterialTheme.colorScheme.primary,
                            0.35f to MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(12.dp).background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hembras", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(12.dp).background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Machos", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Menu options
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MenuCard(
                    icon = Icons.Default.Pets,
                    label = "Registrar Animal",
                    onClick = { onMenuClick("registrar_animal") }
                )
                MenuCard(
                    icon = Icons.Default.EditNote,
                    label = "Registrar Lote",
                    onClick = { onMenuClick("registrar_lote") }
                )
                MenuCard(
                    icon = Icons.Default.Grain,
                    label = "Registrar Alimento",
                    onClick = { onMenuClick("registrar_alimento") }
                )
                MenuCard(
                    icon = Icons.Default.FoodBank,
                    label = "Alimentación",
                    onClick = { onMenuClick("alimentacion") }
                )
                MenuCard(
                    icon = Icons.Default.Home,
                    label = "Potrero",
                    onClick = { onMenuClick("potrero") }
                )
                MenuCard(
                    icon = Icons.Default.BarChart,
                    label = "Reportes",
                    onClick = { onMenuClick("reportes") }
                )
            }
        }
    }

    @Composable
    fun MenuCard(icon: ImageVector, label: String, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = label, style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    /**
     * Simple pie chart composable: receives segments as list of Pair(fraction, color).
     * Fractions should sum to 1f (or less — remainder is left blank).
     */
    @Composable
    fun PieChart(segments: List<Pair<Float, Color>>, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier) {
            val total = size.minDimension
            val stroke = 0f
            val radius = total / 2.0f - stroke
            var startAngle = -90f
            segments.forEach { (fraction, color) ->
                val sweep = fraction * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(
                        (size.width - 2 * radius) / 2f,
                        (size.height - 2 * radius) / 2f
                    ),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
                startAngle += sweep
            }
            // optional white center for donut look — commented out, enable if desired
            // drawCircle(Color.White, radius * 0.4f, center = center)
        }
    }

    @Preview(showBackground = true, widthDp = 360, heightDp = 800)
    @Composable
    fun PreviewMainMenu() {
        MaterialTheme {
            MainMenuScreen(onMenuClick = {})
        }
    }
}
