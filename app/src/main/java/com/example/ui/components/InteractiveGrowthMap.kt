package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SamuraiScreen
import com.example.ui.theme.*

data class GrowthNode(
    val name: String,
    val title: String,
    val targetScreen: SamuraiScreen,
    val progressPercent: Int, // 0 to 100 calculated from real data
    val iconEmoji: String,
    val color: Color,
    val rankBadge: String,
    val relX: Float, // 0.0 to 1.0 on canvas
    val relY: Float
)

@Composable
fun InteractiveGrowthMap(
    nodes: List<GrowthNode>,
    onSelectNode: (SamuraiScreen) -> Unit
) {
    var selectedNode by remember { mutableStateOf<GrowthNode?>(nodes.firstOrNull()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw ethereal background fog
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(CrimsonSun.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(w / 2f, h / 2f),
                            radius = w / 1.5f
                        )
                    )

                    // Draw interconnecting glowing constellation lines between nodes
                    for (i in nodes.indices) {
                        for (j in i + 1 until nodes.size) {
                            val n1 = nodes[i]
                            val n2 = nodes[j]
                            val p1 = Offset(n1.relX * w, n1.relY * h)
                            val p2 = Offset(n2.relX * w, n2.relY * h)

                            val dist = (p1 - p2).getDistance()
                            if (dist < w * 0.55f) {
                                drawLine(
                                    brush = Brush.linearGradient(
                                        listOf(n1.color.copy(alpha = 0.35f), n2.color.copy(alpha = 0.35f)),
                                        start = p1,
                                        end = p2
                                    ),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }

                    // Draw node circles & progress rings
                    nodes.forEach { node ->
                        val pos = Offset(node.relX * w, node.relY * h)
                        val radius = 24.dp.toPx()

                        // Outer Glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(node.color.copy(alpha = 0.4f), Color.Transparent),
                                center = pos,
                                radius = radius * 1.8f
                            ),
                            radius = radius * 1.8f,
                            center = pos
                        )

                        // Base Circle
                        drawCircle(color = DemonSlayerCardElevated, radius = radius, center = pos)

                        // Progress Arc
                        val sweep = (node.progressPercent / 100f) * 360f
                        drawArc(
                            color = node.color,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(pos.x - radius, pos.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                        )
                    }
                }

                // Node tap targets
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val w = maxWidth
                    val h = maxHeight

                    nodes.forEach { node ->
                        val x = w * node.relX - 26.dp
                        val y = h * node.relY - 26.dp

                        Box(
                            modifier = Modifier
                                .offset(x = x, y = y)
                                .size(52.dp)
                                .clip(CircleShape)
                                .clickable { selectedNode = node },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = node.iconEmoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Node Details
        selectedNode?.let { node ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DemonSlayerCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, node.color),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = node.iconEmoji, fontSize = 30.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = node.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = node.color.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${node.progressPercent}% Mastered",
                                        style = MaterialTheme.typography.labelSmall.copy(color = node.color),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = node.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Button(
                        onClick = { onSelectNode(node.targetScreen) },
                        colors = ButtonDefaults.buttonColors(containerColor = node.color),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Open", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
