package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SamuraiScreen
import com.example.ui.theme.*
import kotlin.math.*

data class WorldLocation3D(
    val name: String,
    val japaneseTitle: String,
    val targetScreen: SamuraiScreen,
    val x: Float, // 3D world coordinates
    val y: Float,
    val z: Float,
    val color: Color,
    val iconEmoji: String,
    val description: String
)

@Composable
fun Interactive3DWorldScreen(
    onNavigate: (SamuraiScreen) -> Unit,
    onClose: () -> Unit
) {
    // 3D Camera Angles controlled by user drag gesture
    var yaw by remember { mutableStateOf(0.4f) }
    var pitch by remember { mutableStateOf(0.35f) }
    var zoom by remember { mutableStateOf(1.0f) }

    var selectedLocation by remember { mutableStateOf<WorldLocation3D?>(null) }

    // Particle Animation for Sakura Blossoms
    val infiniteTransition = rememberInfiniteTransition(label = "sakura")
    val blossomTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val locations = remember {
        listOf(
            WorldLocation3D("Central Dojo", "本道場", SamuraiScreen.HOME, 0f, 0f, 0f, CrimsonSun, "⛩️", "Command center & daily overview"),
            WorldLocation3D("Training Grounds", "修練場", SamuraiScreen.HABITS, -180f, -20f, -60f, CrimsonSunGlow, "⚔️", "Daily habits & discipline kata"),
            WorldLocation3D("Mission Board", "任務所", SamuraiScreen.TASKS, 160f, -10f, -80f, FlameGold, "📜", "Tactical tasks & deadlines"),
            WorldLocation3D("Goal Shrine", "誓願祠", SamuraiScreen.GOALS, 0f, 40f, -240f, FlameAmber, "🏮", "Grand ambitions & milestones"),
            WorldLocation3D("Reflection Garden", "瞑想庭", SamuraiScreen.REFLECTION, -220f, 30f, 120f, WaterBlue, "🌸", "Personal journal & self review"),
            WorldLocation3D("Knowledge Temple", "知識殿", SamuraiScreen.ACADEMICS, 200f, 20f, 130f, MistCyan, "📚", "Academic studies & wisdom"),
            WorldLocation3D("Fitness Arena", "鍛錬場", SamuraiScreen.FITNESS, -140f, -40f, 220f, WindEmerald, "🏋️", "Calisthenics & workout dojo"),
            WorldLocation3D("Health Sanctuary", "養生院", SamuraiScreen.HEALTH, 150f, -30f, 210f, InsectPurple, "🌿", "Sleep, recovery & vitality"),
            WorldLocation3D("Observatory", "星見塔", SamuraiScreen.ANALYTICS, 0f, -120f, 250f, ThunderYellow, "🔭", "Global life analytics & graphs"),
            WorldLocation3D("Samurai Chamber", "鬼殺の間", SamuraiScreen.SAMURAI_CHAMBER, 0f, 80f, 160f, NichirinGold, "👑", "Character evolution & Hashira ranks")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    yaw += dragAmount.x * 0.005f
                    pitch = (pitch - dragAmount.y * 0.005f).coerceIn(-0.6f, 0.8f)
                }
            }
    ) {
        // 3D Canvas Rendering
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f + 40f
            val fov = 450f * zoom

            // Draw Mountain & Japanese Sky Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DemonSlayerBlack,
                        DemonSlayerDark,
                        Color(0xFF1A121F),
                        DemonSlayerBlack
                    )
                )
            )

            // Draw 3D Ground Grid (Japanese Stone Pathway Perspective)
            val gridLines = 10
            val gridSpacing = 60f
            for (i in -gridLines..gridLines) {
                val z1 = -300f
                val z2 = 300f
                val x = i * gridSpacing

                val p1 = project3D(x, 60f, z1, yaw, pitch, cx, cy, fov)
                val p2 = project3D(x, 60f, z2, yaw, pitch, cx, cy, fov)

                if (p1 != null && p2 != null) {
                    drawLine(
                        color = DemonSlayerBorder.copy(alpha = 0.25f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1f
                    )
                }
            }

            // Draw Connecting Pathway Beams between Dojo and locations
            val centerProjected = project3D(0f, 0f, 0f, yaw, pitch, cx, cy, fov)
            if (centerProjected != null) {
                locations.forEach { loc ->
                    val lp = project3D(loc.x, loc.y, loc.z, yaw, pitch, cx, cy, fov)
                    if (lp != null) {
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(loc.color.copy(alpha = 0.6f), CrimsonSun.copy(alpha = 0.2f)),
                                start = centerProjected,
                                end = lp
                            ),
                            start = centerProjected,
                            end = lp,
                            strokeWidth = 2.5f
                        )
                    }
                }
            }

            // Draw 3D Nodes (Locations) sorted by depth (Z distance from camera)
            val projectedNodes = locations.mapNotNull { loc ->
                // Apply rotation matrix
                val cosY = cos(yaw)
                val sinY = sin(yaw)
                val cosP = cos(pitch)
                val sinP = sin(pitch)

                val x1 = loc.x * cosY + loc.z * sinY
                val z1 = -loc.x * sinY + loc.z * cosY
                val y1 = loc.y * cosP - z1 * sinP
                val zRot = loc.y * sinP + z1 * cosP + 450f

                if (zRot > 40f) {
                    val scale = fov / zRot
                    val screenX = cx + x1 * scale
                    val screenY = cy + y1 * scale
                    Triple(loc, Offset(screenX, screenY), zRot)
                } else null
            }.sortedByDescending { it.third } // Back to front depth sorting

            projectedNodes.forEach { (loc, pos, zDepth) ->
                val nodeRadius = (28f * (fov / zDepth)).coerceIn(16f, 54f)

                // Glow Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(loc.color.copy(alpha = 0.6f), Color.Transparent),
                        center = pos,
                        radius = nodeRadius * 2.2f
                    ),
                    radius = nodeRadius * 2.2f,
                    center = pos
                )

                // Node Body
                drawCircle(
                    color = DemonSlayerCardElevated,
                    radius = nodeRadius,
                    center = pos
                )
                drawCircle(
                    color = loc.color,
                    radius = nodeRadius,
                    center = pos,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                )
            }

            // Floating Cherry Blossom Petals (Sakura Particles)
            val randomSeed = 42
            val petalCount = 35
            for (i in 0 until petalCount) {
                val seedOffset = (i * 97) % 1000
                val progress = ((blossomTime + seedOffset) % 1000) / 1000f

                val px = (sin(i * 1.7f + progress * 6.28f) * 180f + (i * 29) % size.width)
                val py = (progress * (size.height + 60f)) - 30f
                val petalRadius = 4f + (i % 4)

                drawCircle(
                    color = LovePink.copy(alpha = 0.5f + 0.3f * sin(progress * 3.14f)),
                    center = Offset(px, py),
                    radius = petalRadius
                )
            }
        }

        // Overlay Header & Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DEMON SLAYER 3D REALM",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = CinzelFontFamily,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = FlameGold
                    )
                    Text(
                        text = "Drag to rotate • Tap a location below to fly & enter",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DemonSlayerCard)
                        .testTag("btn_close_3d_world")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit 3D World", tint = TextPrimary)
                }
            }
        }

        // Location Quick Travel Carousel at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            selectedLocation?.let { loc ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DemonSlayerCard.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, loc.color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = loc.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = loc.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = loc.japaneseTitle, style = MaterialTheme.typography.bodySmall, color = loc.color)
                                }
                                Text(
                                    text = loc.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Button(
                            onClick = { onNavigate(loc.targetScreen) },
                            colors = ButtonDefaults.buttonColors(containerColor = loc.color),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enter", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
                        }
                    }
                }
            }

            // Grid of location selector buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                locations.forEach { loc ->
                    val isSelected = selectedLocation?.name == loc.name
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) loc.color.copy(alpha = 0.25f) else DemonSlayerCardElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) loc.color else DemonSlayerBorder
                        ),
                        modifier = Modifier.clickable {
                            selectedLocation = loc
                            // Animate camera pitch and yaw towards target
                            yaw = -atan2(loc.x, loc.z.coerceAtLeast(10f)) * 0.7f
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = loc.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loc.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3D Perspective Projection Function
fun project3D(
    x: Float,
    y: Float,
    z: Float,
    yaw: Float,
    pitch: Float,
    cx: Float,
    cy: Float,
    fov: Float
): Offset? {
    val cosY = cos(yaw)
    val sinY = sin(yaw)
    val cosP = cos(pitch)
    val sinP = sin(pitch)

    val x1 = x * cosY + z * sinY
    val z1 = -x * sinY + z * cosY
    val y1 = y * cosP - z1 * sinP
    val zRot = y * sinP + z1 * cosP + 450f

    if (zRot <= 20f) return null

    val scale = fov / zRot
    val screenX = cx + x1 * scale
    val screenY = cy + y1 * scale
    return Offset(screenX, screenY)
}
