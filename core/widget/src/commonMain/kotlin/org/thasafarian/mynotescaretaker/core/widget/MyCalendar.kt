package org.thasafarian.mynotescaretaker.core.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import org.thasafarian.mynotescaretaker.core.util.PlatformUtils
import org.thasafarian.mynotescaretaker.core.util.WindowType
import org.thasafarian.mynotescaretaker.core.util.rememberWindowType
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalAnimationApi::class, ExperimentalTime::class)
@Composable
fun MyCalendar() {

    val colorScheme = MaterialTheme.colorScheme

    var currentDate by remember {
        mutableStateOf(
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        )
    }

    var currentYear by remember {
        mutableStateOf(
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .year
        )
    }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentDate = currentDate.minus(1, DateTimeUnit.MONTH)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = colorScheme.onBackground
                )
            }

            Text(
                text = currentDate.month.name.lowercase()
                    .replaceFirstChar { it.uppercase() } + " $currentYear",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            IconButton(onClick = {
                currentDate = currentDate.plus(1, DateTimeUnit.MONTH)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val windowType = rememberWindowType()
        val isPhoneRotated = PlatformUtils.isAndroid && windowType != WindowType.Compact

        // Calendar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!isPhoneRotated) Modifier.fillMaxHeight(0.5f)
                    else Modifier.wrapContentHeight()
                )
        ) {
            AnimatedContent(
                targetState = currentDate,
                transitionSpec = {
                    val isNext = targetState > initialState

                    val enter = slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> if (isNext) fullWidth else -fullWidth }
                    ) + fadeIn(animationSpec = tween(300))

                    val exit = slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> if (isNext) -fullWidth else fullWidth }
                    ) + fadeOut(animationSpec = tween(300))

                    enter togetherWith exit
                },
                label = "Calendar Animation"
            ) { currentDate ->
                val daysInMonth = remember(currentDate) {
                    val firstDay = LocalDate(currentDate.year, currentDate.month, 1)
                    val lastDayOfMonth = when (currentDate.month) {
                        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
                        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
                        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
                        Month.FEBRUARY -> if (isLeapYear(currentDate.year)) 29 else 28
                    }

                    val lastDay = LocalDate(currentDate.year, currentDate.month, lastDayOfMonth)
                    (firstDay..lastDay).toList()
                }

                CalendarGrid(
                    daysInMonth = daysInMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    colorScheme = colorScheme
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedDate?.let { "Selected date: $it" } ?: "No date selected",
            fontSize = 16.sp,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarGrid(
    daysInMonth: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    colorScheme: ColorScheme
) {
    val firstDayOfWeek = (daysInMonth.first().dayOfWeek.isoDayNumber % 7)
    val totalDays = daysInMonth.size + firstDayOfWeek
    val rows = (totalDays / 7.0).roundToInt()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek
                    if (dayIndex in daysInMonth.indices) {
                        val day = daysInMonth[dayIndex]
                        val isSelected = day == selectedDate
                        val isToday = day == Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> colorScheme.primary
                                        isToday -> colorScheme.primary.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                color = if (isSelected)
                                    colorScheme.onPrimary
                                else
                                    colorScheme.onBackground,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    } else {
                        Box(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

