package org.thasafarian.mynotescaretaker.core.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.thasafarian.mynotescaretaker.core.data.model.Candidate
import org.thasafarian.mynotescaretaker.core.data.model.GeminiContent
import org.thasafarian.mynotescaretaker.core.data.model.GeminiPart
import org.thasafarian.mynotescaretaker.core.data.model.GeminiRequest
import org.thasafarian.mynotescaretaker.core.data.model.GeminiResponse
import org.thasafarian.mynotescaretaker.core.domain.model.AiTaskSuggestion
import org.thasafarian.mynotescaretaker.core.domain.model.Task
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class TaskRepository(
    private val client: HttpClient = createHttpClient()
) {
    private val baseUrl = Config.baseUrl
    private val geminiUrl = "${Config.geminiBaseUrl}/${Config.geminiModel}"
    private val apiKey = Config.geminiApiKey

    suspend fun fetchTasks(): List<Task> {
        return client.get(baseUrl).body()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun fetchTasksGrouped(): Map<String, List<Task>> {
        val tasks: List<Task> = client.get(baseUrl).body()

        // Always compare in UTC
        val nowUtc = Clock.System.now()
        val todayUtc = nowUtc.toLocalDateTime(TimeZone.UTC).date

        return tasks.groupBy { task ->
            val createdInstant = Instant.parse(task.createdAt)
            val createdUtc = createdInstant.toLocalDateTime(TimeZone.UTC)
            val createdDate = createdUtc.date

            val daysDiff = createdDate.daysUntil(todayUtc)


            when (daysDiff) {
                0 -> "Today"
                1 -> "Yesterday"
                -1 -> "Tomorrow"
                in 2..6 -> "$daysDiff days ago"
                in -6..-2 -> "In ${-daysDiff} days"
                else -> {
                    val monthName = createdDate.month.name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                    "${createdDate.dayOfMonth} $monthName ${createdDate.year}"
                }
            }
        }
    }

    suspend fun postTask(task: Task): Task {
        return client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(task)
        }.body()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun postGroupedTask(task: Task): Map<String, List<Task>> {
        // Post new task to backend
        client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(task)
        }

        // Then fetch the updated list and group it
        val updatedTasks: List<Task> = client.get(baseUrl).body()

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        return updatedTasks.groupBy { item ->
            val createdDate = Instant.parse(item.createdAt)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            val daysDiff = createdDate.daysUntil(today)

            when (daysDiff) {
                0 -> "Today"
                1 -> "Yesterday"
                -1 -> "Tomorrow"
                in 2..6 -> "${daysDiff} days ago"
                in -6..-2 -> "In ${-daysDiff} days"
                else -> {
                    val monthName = createdDate.month.name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                    "${createdDate.dayOfMonth} $monthName ${createdDate.year}"
                }
            }
        }
    }


    suspend fun deleteTask(id: String): Boolean {
        return try {
            client.delete("${baseUrl}/$id")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateTask(id: String, updatedTask: Task): Task {
        return client.put("$baseUrl/$id") {
            contentType(ContentType.Application.Json)
            setBody(updatedTask)
        }.body()
    }

    @OptIn(ExperimentalTime::class)
    suspend fun suggestTasks(userPrompt: String): Map<String, List<Task>>? {
        val tasks = fetchTasks()
        val taskText = tasks.joinToString("\n") {
            "- id: ${it.id} title: ${it.title}" +
                    " created at" +
                    " ${it.createdAt} (status: ${if (it.status == 1)
                        "done" else "pending"})"
        }

        val fullPrompt = """
        You are a productivity assistant.

        Here is the user's to-do list:
        $taskText

        User prompt: "$userPrompt"

        Please analyze the tasks and suggest what the user should focus on today.

        ⚠️ Output rules:
        - Respond **only** in JSON format.
        - Do not include any explanation or text outside the JSON.
        - Use this exact structure (array only, no wrapping object):
        [
          {
            "createdAt": "string (ISO-8601 datetime)",
            "title": "string",
            "status": 0 or 1,
            "id": "string"
          }
        ]
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = fullPrompt))
                )
            )
        )

        val response: HttpResponse = client.post(geminiUrl) {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status.isSuccess()) {
            val jsonDecoder = Json {
                ignoreUnknownKeys = true
            }

            val geminiResponse: GeminiResponse = jsonDecoder.decodeFromString(response.bodyAsText())

            var text = geminiResponse.candidates
                .firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text ?: return null

            // 🔧 Clean Markdown formatting (remove ```json or ``` etc)
            text = text
                .replace(Regex("```json", RegexOption.IGNORE_CASE), "")
                .replace("```", "")
                .trim()

            // ✅ Decode cleaned JSON as list of Task
            val updatedTasks = jsonDecoder
                .decodeFromString(text) as List<Task>

            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date

            return updatedTasks.groupBy { item ->
                val createdDate = Instant.parse(item.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val daysDiff = createdDate.daysUntil(today)

                when (daysDiff) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    -1 -> "Tomorrow"
                    in 2..6 -> "${daysDiff} days ago"
                    in -6..-2 -> "In ${-daysDiff} days"
                    else -> {
                        val monthName = createdDate.month.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                        "${createdDate.dayOfMonth} $monthName ${createdDate.year}"
                    }
                }
            }
        } else {
            println("Error AI Gemini: ${response.status}")
            return null
        }
    }

    suspend fun suggestTasks3(userPrompt: String): List<Task>? {
        val tasks = fetchTasks()
        val taskText = tasks.joinToString("\n") {
            "- id: ${it.id} title: ${it.title} created at ${it.createdAt} (status: ${if (it.status == 1) "done" else "pending"})"
        }

        val fullPrompt = """
        You are a productivity assistant.

        Here is the user's to-do list:
        $taskText

        User prompt: "$userPrompt"

        Please analyze the tasks and suggest what the user should focus on today.

        ⚠️ Output rules:
        - Respond **only** in JSON format.
        - Do not include any explanation or text outside the JSON.
        - Use this exact structure (array only, no wrapping object):
        [
          {
            "createdAt": "string (ISO-8601 datetime)",
            "title": "string",
            "status": 0 or 1,
            "id": "string"
          }
        ]
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = fullPrompt))
                )
            )
        )

        val response: HttpResponse = client.post(geminiUrl) {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status.isSuccess()) {
            val jsonDecoder = Json {
                ignoreUnknownKeys = true
            }

            val geminiResponse: GeminiResponse = jsonDecoder.decodeFromString(response.bodyAsText())

            var text = geminiResponse.candidates
                .firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text ?: return null

            // 🔧 Clean Markdown formatting (remove ```json or ``` etc)
            text = text
                .replace(Regex("```json", RegexOption.IGNORE_CASE), "")
                .replace("```", "")
                .trim()

            // ✅ Decode cleaned JSON as list of Task
            return jsonDecoder.decodeFromString(text)
        } else {
            println("Error: ${response.status}")
            return null
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun suggestedTasks(userPrompt: String): List<Pair<String, Map<String, List<Task>>>>? {
        val tasks = fetchTasks()
        val taskText = tasks.joinToString("\n") {
            "- id: ${it.id}, title: ${it.title}, createdAt: ${it.createdAt}, status: ${if (it.status == 1) "done" else "pending"}"
        }

        val fullPrompt = """
        You are a productivity assistant helping users manage their to-do list.

        Here is the user's to-do list:
        $taskText

        User prompt: "$userPrompt"

        Your task:
        - Suggest what the user should focus on today (or upcoming days).
        - Provide a short motivational text.
        - Attach relevant task lists.

        ⚠️ Output rules:
        - Respond only in JSON.
        - No explanation outside the JSON.
        - Use this structure (array only):
        [
          {
            "suggestion": "string",
            "tasks": [
              {
                "id": "string",
                "title": "string",
                "status": 0 or 1,
                "createdAt": "string (ISO-8601 datetime)"
              }
            ]
          }
        ]
        - and please answer with suitable language based on user prompts
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = fullPrompt))))
        )

        val response: HttpResponse = client.post(geminiUrl) {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            println("Error AI Gemini: ${response.status}")
            return null
        }

        val jsonDecoder = Json { ignoreUnknownKeys = true }
        val geminiResponse: GeminiResponse = jsonDecoder.decodeFromString(response.bodyAsText())

        var text = geminiResponse.candidates
            .firstOrNull()
            ?.content?.parts?.firstOrNull()
            ?.text ?: return null

        text = text
            .replace(Regex("```json", RegexOption.IGNORE_CASE), "")
            .replace("```", "")
            .trim()

        val suggestions: List<AiTaskSuggestion> = jsonDecoder.decodeFromString(text)

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        fun groupTasksByDate(tasks: List<Task>): Map<String, List<Task>> {
            return tasks.groupBy { item ->
                val createdDate = Instant.parse(item.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val daysDiff = createdDate.daysUntil(today)
                when (daysDiff) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    -1 -> "Tomorrow"
                    in 2..6 -> "${daysDiff} days ago"
                    in -6..-2 -> "In ${-daysDiff} days"
                    else -> {
                        val monthName = createdDate.month.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                        "${createdDate.dayOfMonth} $monthName ${createdDate.year}"
                    }
                }
            }
        }

        return suggestions.map { suggestion ->
            suggestion.suggestion to groupTasksByDate(suggestion.tasks)
        }
    }


}

