package com.example.runlogger

import com.google.gson.annotations.SerializedName

// Root Request
data class NotionPageRequest(
    val parent: DatabaseParent,
    val properties: RunProperties
)

// Parent (the database)
data class DatabaseParent(val database_id: String)

// Properties of said parent
data class RunProperties(
    @SerializedName("Date")            val date: DateValue,
    @SerializedName("Distance")        val distance: NumberValue,
    @SerializedName("Time (text)")     val time: List<RichTextValue>,
    @SerializedName("Avg Speed(km/h)") val speed: NumberValue,
    @SerializedName("Avg Pace(km)")    val pace: List<RichTextValue>,
    @SerializedName("Est. Calories")   val calories: NumberValue
)

// Value wrappers
data class NumberValue(val number: Double?)

data class DateValue(val date: DateDetail)
data class DateDetail(val start: String)

data class RichTextValue(val text: TextContent)
data class TextContent(val content: String?)