package com.example.runlogger

// Libraries
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

// tells the IDE to shut the fuck up about snake_case
@Suppress("PropertyName", "LocalVariableName", "FunctionName")

// Main Activity
class MainActivity : AppCompatActivity() {

    // variables for the parsed data
    private var distance_val: Double? = null
    private var time_val: String? = null
    private var pace_val: String? = null
    private var speed_val: Double? = null
    private var calories_val: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate: App started")

        // Initializes the Dropdowns and label fields
        setup_dropdowns()
        setup_tiles()

        // LOG TO NOTION button
        val log_btn: Button = findViewById(R.id.log_button)
        val feedback_text: TextView = findViewById(R.id.feedback)

        // Android's photo picker logic
        val image_select = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                try {
                    // Convert the Uri into an InputImage
                    val image = InputImage.fromFilePath(this, uri)

                    // Latin Text Recognizer
                    val recognizer =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    // Feed the image to the recognizer
                    recognizer.process(image).addOnSuccessListener { scanned_text ->
                        // Raw scanned text
                        val raw_string = scanned_text.text

                        // Regex definitions
                        val distance_regex = Regex("""\b(?!\d{4})\d{3}|\b\d{1}[.]\d{2}""")
                        val time_regex     = Regex(""":[\dO]{2}:[\dO]{2}""")
                        val pace_regex     = Regex("""\b[\dO]:[\dO]{2}\b(?!\:)(?![ ]*[A-Za-z])""")
                        val speed_regex    = Regex("""\d{1,2}\.?\d*\s*k""")
                        val calories_regex = Regex("""\b(?!784\b)\d{2,4}\b""")

                        // Pattern search
                        val distance_res = distance_regex.find(raw_string)
                        val time_res = time_regex.find(raw_string)
                        val pace_res = pace_regex.find(raw_string)
                        val speed_res = speed_regex.find(raw_string)
                        val calories_res = calories_regex.findAll(raw_string).lastOrNull()

                        // Value extraction & String-to-Double conversion
                        distance_val = distance_res?.value?.toDoubleOrNull()
                        time_val = time_res?.value?.drop(1)
                        pace_val = pace_res?.value
                        speed_val = speed_res?.value?.dropLast(2)?.toDoubleOrNull()
                        calories_val = calories_res?.value?.toDoubleOrNull()

                        // UI update
                        update_ui_tiles()

                        // LOG TO NOTION button unlock logic
                        val is_data_ready = listOf(
                            distance_val,
                            time_val,
                            pace_val,
                            speed_val,
                            calories_val
                        ).none { it == null }

                        if (!is_data_ready) {
                            log_press(true, log_btn)
                            Log.d("Lock", "Data incomplete, check 'Parser' logs")
                        } else {
                            log_press(false, log_btn)
                        }

                        // Debugging shit
                        Log.d(
                            "Parser",
                            "Distance: $distance_val, Time: $time_val, Speed: $speed_val"
                        )
                    }
                        .addOnFailureListener { e ->
                            Log.e("MLKit", "Scanner failed", e)
                        }

                } catch (e: Exception) {
                    Log.e("PhotoPicker", "Failed to load image", e)
                }
            }
        }

        // "SELECT PHOTO" Button.
        val select_photo_btn: Button = findViewById(R.id.upload_button)
        select_photo_btn.setOnClickListener {
            Log.d("MainActivity", "Select Photo button clicked")
            // calls photo picker
            image_select.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        // "LOG TO NOTION" Button listener logic
        log_btn.setOnClickListener {
            Log.d("MainActivity", "Log to Notion button clicked")

            // Time prep
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val current_date = sdf.format(Date())

            // Button lock
            log_press(true, log_btn)

            feedback_text.text = "Attempting to log..."

            // Build the Notion request
            val request = NotionPageRequest(
                parent = DatabaseParent("2a662eb7e82a81aebfafe93cf5231066"),
                properties = RunProperties(
                    date = DateValue(DateDetail(current_date)),
                    distance = NumberValue(distance_val),
                    time = listOf(RichTextValue(TextContent(time_val))),
                    speed = NumberValue(speed_val),
                    pace = listOf(RichTextValue(TextContent(pace_val))),
                    calories = NumberValue(calories_val)
                )
            )

            // Launch network call in background thread
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.create_run_page(request)
                    if (response.isSuccessful) {
                        feedback_text.text = "Log Successful!"
                    } else {
                        feedback_text.text = "Error: ${response.code()}"
                    }
                } catch (e: Exception) {
                    feedback_text.text = "Connection Failed"
                }
            }
        }
    }

    // Function that updates tile values on the UI
    private fun update_ui_tiles() {
        val stats_map = mapOf(
            R.id.tile_distance to distance_val?.toString(),
            R.id.tile_time     to time_val,
            R.id.tile_pace     to pace_val,
            R.id.tile_speed    to speed_val?.toString(),
            R.id.tile_calories to calories_val?.toString()
        )

        for ((tile_id, value) in stats_map) {
            val tile_view = findViewById<LinearLayout>(tile_id)
            tile_view.findViewById<TextView>(R.id.tile_value).text = value ?: "ERROR"
        }
    }

    // Function that handles the dropdown logic
    private fun setup_dropdowns() {
        Log.d("MainActivity", "Setting up dropdowns")
        // Array of the dropdown options
        val run_types = arrayOf("Recovery", "Speed", "Distance", "Running Machine")
        val effort_levels = arrayOf("Low", "Medium", "High")

        // Adapters for the dropdown options (makes them work, basically)
        val type_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, run_types)
        val effort_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, effort_levels)

        // Dropdown variables
        val type_dropdown: AutoCompleteTextView = findViewById(R.id.dropdown_run_type)
        val effort_dropdown: AutoCompleteTextView = findViewById(R.id.dropdown_effort)

        // Sets the adapters for the dropdowns
        type_dropdown.setAdapter(type_adapter)
        effort_dropdown.setAdapter(effort_adapter)

        // Forces the floating label to drop back down when the menu closes
        type_dropdown.setOnDismissListener {
            type_dropdown.clearFocus()
        }
        effort_dropdown.setOnDismissListener {
            effort_dropdown.clearFocus()
        }
    }

    // Function that handles the tile logic
    private fun setup_tiles() {
        Log.d("MainActivity", "Setting up UI tiles")
        val distance_label: TextView = findViewById<android.view.View>(R.id.tile_distance).findViewById(R.id.tile_label)
        val time_label: TextView = findViewById<android.view.View>(R.id.tile_time).findViewById(R.id.tile_label)
        val pace_label: TextView = findViewById<android.view.View>(R.id.tile_pace).findViewById(R.id.tile_label)
        val speed_label: TextView = findViewById<android.view.View>(R.id.tile_speed).findViewById(R.id.tile_label)
        val calories_label: TextView = findViewById<android.view.View>(R.id.tile_calories).findViewById(R.id.tile_label)

        distance_label.text = "DISTANCE"
        time_label.text = "TIME"
        pace_label.text = "PACE"
        speed_label.text = "SPEED"
        calories_label.text = "CALORIES"
    }

    private fun log_press(flag: Boolean, button: Button) {
        // Lock
        if (flag) {
            button.isEnabled = false
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.surface_card))
            button.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }

        // Unlock
        else {
            button.isEnabled = true
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_green))
            button.setTextColor(Color.BLACK)
        }
    }
}