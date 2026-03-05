package com.example.runlogger

import com.google.gson.annotations.SerializedName

// Root Request
data class NotionPageRequest(
    val parent: DatabaseParent,
    val properties: RunProperties
)

data class DatabaseParent(val database_id: String)

data class RunProperties(
    @SerializedName("Date")            val date: DateProperty,
    @SerializedName("Distance")        val distance: NumberProperty,
    @SerializedName("Time (text)")     val timeText: RichTextProperty,
    @SerializedName("Avg Speed(km/h)") val avgSpeed: NumberProperty,
    @SerializedName("Avg Pace(km)")    val avgPace: RichTextProperty,
    @SerializedName("Est. Calories")   val calories: NumberProperty,
    @SerializedName("Type")            val type: SelectProperty,
    @SerializedName("Effort")          val effort: SelectProperty
)

// Property Type Wrappers
data class DateProperty(val date: DateValue)
data class DateValue(val start: String)

data class NumberProperty(val number: Double?)

data class RichTextProperty(@SerializedName("rich_text") val richText: List<RichTextValue>)
data class RichTextValue(val text: TextContent)
data class TextContent(val content: String?)

data class SelectProperty(val select: SelectValue)
data class SelectValue(val name: String)