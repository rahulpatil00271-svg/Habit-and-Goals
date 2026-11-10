package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreathingPathsData
import com.example.data.model.DemonSlayerCharacter
import com.example.data.model.DemonSlayerDatabase
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*

@Composable
fun SamuraiChamberScreen(
    viewModel: SamuraiViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile
    val currentLevel = uiState.journeyWeek
    val activeCharId = userProfile?.characterId ?: "tanjiro"
    val activeChar = DemonSlayerDatabase.getCharacter(activeCharId)

    var previewChar by remember(activeCharId) { mutableStateOf(activeChar) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_samurai_chamber"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SAMURAI EVOLUTION CHAMBER",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = "Demon Slayer Character & Breathing Form",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "52-Week Journey: 1 Week = 1 Progression Level. Current: Level $currentLevel (${uiState.rankTitle}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Active Character Showcase Card
        item {
            val isUnlocked = currentLevel >= previewChar.unlockLevel
            val isCurrentlyActive = previewChar.id == activeCharId

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(2.dp, previewChar.themeColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(previewChar.themeColor.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = previewChar.japaneseName,
                                    style = MaterialTheme.typography.labelSmall.copy(color = previewChar.themeColor, fontSize = 13.sp),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = previewChar.name,
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = previewChar.title,
                                    style = MaterialTheme.typography.titleSmall.copy(color = FlameGold)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(DemonSlayerBlack)
                                    .border(2.dp, previewChar.themeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = previewChar.iconSymbol, fontSize = 32.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"${previewChar.quote}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Breathing Style & Forms
                        Text(
                            text = "⚔️ BREATHING TECHNIQUE: ${previewChar.breathingStyle}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, fontWeight = FontWeight.Bold),
                            color = FlameGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        previewChar.formNames.forEach { form ->
                            Text(
                                text = "• $form",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selection Action
                        if (isCurrentlyActive) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = previewChar.themeColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, previewChar.themeColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Currently Active Awakened Form ✓",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = previewChar.themeColor,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else if (isUnlocked) {
                            Button(
                                onClick = {
                                    viewModel.selectCharacter(previewChar.id, previewChar.breathingStyle)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = previewChar.themeColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Awaken & Equip This Slayer", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DemonSlayerDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🔒 Unlocks at Journey Level ${previewChar.unlockLevel} (Week ${previewChar.unlockLevel})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Demon Slayer Roster Grid
        item {
            Text(
                text = "👥 ALL DEMON SLAYER CHARACTERS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        items(DemonSlayerDatabase.characters) { character ->
            val isUnlocked = currentLevel >= character.unlockLevel
            val isEquipped = character.id == activeCharId
            val isSelected = character.id == previewChar.id

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DemonSlayerCardElevated else DemonSlayerCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isEquipped) FlameGold else if (isSelected) character.themeColor else DemonSlayerBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { previewChar = character }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) character.themeColor.copy(alpha = 0.2f) else DemonSlayerDark)
                                .border(1.5.dp, if (isUnlocked) character.themeColor else DemonSlayerBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isUnlocked) character.iconSymbol else "🔒", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = character.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isUnlocked) TextPrimary else TextTertiary
                                )
                                if (isEquipped) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = FlameGold.copy(alpha = 0.2f)) {
                                        Text("ACTIVE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = FlameGold), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(
                                text = character.breathingStyle,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isUnlocked) character.themeColor else TextTertiary
                            )
                        }
                    }

                    Text(
                        text = if (isUnlocked) "Level ${character.unlockLevel}" else "Req Lv.${character.unlockLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUnlocked) FlameGold else TextTertiary
                    )
                }
            }
        }

        // Breathing Paths Section
        item {
            Text(
                text = "🌬️ ACTIVE BREATHING PATHS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        items(BreathingPathsData.paths) { path ->
            val isCurrentPath = userProfile?.activeBreathingPath == path.name

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrentPath) path.color else DemonSlayerBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectBreathingPath(path.name) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = path.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${path.name} Breathing",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isCurrentPath) path.color else TextPrimary
                            )
                            Text(text = path.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    if (isCurrentPath) {
                        Text("Active Path", color = path.color, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
