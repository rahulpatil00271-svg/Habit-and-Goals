package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntity
import com.example.data.model.ReflectionAnswerEntity
import com.example.data.model.SelfReflectionEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.*

@Composable
fun ReflectionScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    var activeSubSection by remember { mutableStateOf(0) } // 0: Journal, 1: Daily 10 Questions, 2: Self-Reflection Pillars
    val journals by viewModel.journalsFlow.collectAsState(initial = emptyList())
    val reflectionAnswers by viewModel.reflectionAnswersFlow.collectAsState(initial = emptyList())
    val selfReflections by viewModel.selfReflectionsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    val defaultQuestions = listOf(
        "1. What did I accomplish today?",
        "2. What did I learn today?",
        "3. What mistakes did I make?",
        "4. What could I have done better?",
        "5. What am I grateful for?",
        "6. Did I follow my daily habits?",
        "7. What challenged me today?",
        "8. What did I avoid doing?",
        "9. What is my biggest priority tomorrow?",
        "10. How would I rate my day out of 10?"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_reflection"),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REFLECTION GARDEN",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = WaterBlue
                            )
                            Text(
                                text = "Inner Temple & Diary",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        if (activeSubSection == 0) {
                            Button(
                                onClick = onOpenQuickAdd,
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "New Scroll", tint = DemonSlayerBlack, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Scroll", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector
                    TabRow(
                        selectedTabIndex = activeSubSection,
                        containerColor = DemonSlayerDark,
                        contentColor = WaterBlue,
                        divider = {},
                        indicator = {}
                    ) {
                        listOf("Journal Scrolls", "Daily Questions", "Self-Reflection").forEachIndexed { index, title ->
                            val isSelected = activeSubSection == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) WaterBlue.copy(alpha = 0.2f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, WaterBlue) else null,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clickable { activeSubSection = index }
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) WaterBlueGlow else TextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Journal Scrolls
        if (activeSubSection == 0) {
            if (journals.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📜", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No journal scrolls inscribed yet.", color = TextPrimary)
                            Text("Inscribe your first entry using 'New Scroll'.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(journals) { journal ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = journal.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = WaterBlue.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = journal.mood,
                                        style = MaterialTheme.typography.labelSmall.copy(color = WaterBlueGlow),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Recorded on: ${journal.dateStr}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = journal.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteJournal(journal) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Daily 10 Reflection Questions
        if (activeSubSection == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DAILY REFLECTION AUDIT ($todayStr)",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily),
                            color = WaterBlueGlow
                        )
                        Text(
                            text = "Answer each question to refine your blade and spirit. Answers are saved per date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            items(defaultQuestions.indices.toList()) { index ->
                val qText = defaultQuestions[index]
                val existingAnswer = reflectionAnswers.find { it.dateStr == todayStr && it.questionIndex == index }?.answerText ?: ""
                var answerInput by remember(existingAnswer) { mutableStateOf(existingAnswer) }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = qText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = answerInput,
                            onValueChange = { answerInput = it },
                            placeholder = { Text("Record your reflection...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = customTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = {
                                    if (answerInput.isNotBlank()) {
                                        viewModel.saveReflectionAnswer(todayStr, index, qText, answerInput.trim())
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WaterBlue.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Save Answer", color = WaterBlueGlow, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Self-Reflection Pillars
        if (activeSubSection == 2) {
            val pillars = listOf(
                "STRENGTHS" to "My Core Strengths",
                "WEAKNESSES" to "My Current Weaknesses",
                "MISTAKES" to "Mistakes & Lessons Learned",
                "FEARS" to "Fears to Overcome",
                "VALUES" to "My Unshakable Values",
                "ASPIRATIONS" to "Long-Term Future Aspirations"
            )

            items(pillars) { (key, title) ->
                val existing = selfReflections.find { it.sectionKey == key }
                var textContent by remember(existing) { mutableStateOf(existing?.content ?: "") }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FlameGold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            colors = customTextFieldColors(),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = {
                                    viewModel.saveSelfReflection(key, title, textContent.trim())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlameGold.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Inscribe Pillar", color = FlameGold, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
