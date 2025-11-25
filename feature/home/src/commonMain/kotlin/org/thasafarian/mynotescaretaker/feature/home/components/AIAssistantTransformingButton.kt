package org.thasafarian.mynotescaretaker.feature.home.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AIAssistantTransformingButton(
    onClick: (userPrompt:String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var typedText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val transition = updateTransition(targetState = expanded, label = "buttonTransform")
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Animations
    val cornerRadius by transition.animateDp(
        transitionSpec = { tween(durationMillis = 600, easing = FastOutLinearInEasing) },
        label = "cornerRadius"
    ) { if (it) 20.dp else 50.dp }

    val backgroundColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 600) },
        label = "backgroundColor"
    ) { if (it) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary }

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
        label = "scale"
    ) { if (it) 1f else 0.9f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 400) },
        label = "overlayAlpha"
    ) { if (it) 0.8f else 0f }

    val randomPlaceholdersTemplate = listOf(
        "Show my tasks for today",
        "Show my pending tasks"
    )
    var randomPlaceholder by remember { mutableStateOf(randomPlaceholdersTemplate.random()) }

    LaunchedEffect(expanded) {
        if (expanded) {
            delay(400)
            focusRequester.requestFocus()
            keyboardController?.show()
            randomPlaceholder = randomPlaceholdersTemplate.random()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
                    .clickable(enabled = true, onClick = {

                    }) // block clicks behind
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .imePadding(),
            contentAlignment = if (expanded) Alignment.TopEnd  else Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(backgroundColor)
                    .padding(if (expanded) 8.dp else 0.dp)
                    .animateContentSize()
                    .alpha(scale),
                contentAlignment = Alignment.Center
            ) {
                if (!expanded) {
                    Button(
                        onClick = {

                            expanded = true
                            typedText = ""
                            inputText = ""
                            scope.launch {
                                delay(400)
                                val fullText = "What can I do for you?"
                                for (i in fullText.indices) {
                                    typedText = fullText.take(i + 1)
                                    delay(20)
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = "AI Assistant",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Assistant", color = Color.White)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxHeight()
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                typedText,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { expanded = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = {
                                    Text(
                                        text = randomPlaceholder,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                },
                                textStyle = TextStyle(fontSize = 16.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Send
                                )
                            )

                            IconButton(
                                onClick = {
                                    expanded = false
                                    keyboardController?.hide()
                                    onClick(inputText)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

