package com.example.composecollectwords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composecollectwords.ui.theme.ComposeCollectWordsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCollectWordsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    var values by remember { mutableStateOf(listOf<Float>()) }

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                input.toFloatOrNull()?.let {
                    values = values + it
                    input = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Insert value")
        }
        RegressionChart(values, Modifier.padding(top = 16.dp))
    }
}

@Composable
fun RegressionChart(points: List<Float>, modifier: Modifier = Modifier) {
    if (points.isEmpty()) return

    val xs = points.indices.map { it.toDouble() }
    val ys = points.map { it.toDouble() }
    val xMean = xs.average()
    val yMean = ys.average()
    val numerator = xs.zip(ys).sumOf { (x, y) -> (x - xMean) * (y - yMean) }
    val denominator = xs.sumOf { (x - xMean) * (x - xMean) }
    val slope = if (denominator == 0.0) 0.0 else numerator / denominator
    val intercept = yMean - slope * xMean
    val y0 = intercept
    val yN = slope * xs.last() + intercept

    val allY = ys + listOf(y0, yN)
    val minY = allY.minOrNull() ?: 0.0
    val maxY = allY.maxOrNull() ?: 0.0
    val yRange = (maxY - minY).takeIf { it != 0.0 } ?: 1.0
    val xMax = xs.lastOrNull() ?: 1.0

    Canvas(
        modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFEFEFEF))
    ) {
        val xScale = size.width / xMax.toFloat().coerceAtLeast(1f)
        val yScale = size.height / yRange.toFloat()
        points.forEachIndexed { index, y ->
            val xPos = index * xScale
            val yPos = size.height - ((y - minY).toFloat() * yScale)
            drawCircle(Color.Blue, radius = 4.dp.toPx(), center = Offset(xPos, yPos))
        }
        val start = Offset(
            0f,
            size.height - ((y0 - minY).toFloat() * yScale)
        )
        val end = Offset(
            xMax.toFloat() * xScale,
            size.height - ((yN - minY).toFloat() * yScale)
        )
        drawLine(Color.Red, start, end, strokeWidth = 2.dp.toPx())
    }
}

