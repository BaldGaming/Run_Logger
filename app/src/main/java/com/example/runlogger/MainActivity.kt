package com.example.runlogger

// Libraries
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.core.content.ContextCompat
import android.graphics.Color

// Main Activity
class MainActivity : AppCompatActivity() {
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

        // Android's photo picker logic
        val image_select = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                try {
                    // Convert the Uri into an InputImage
                    val image = com.google.mlkit.vision.common.InputImage.fromFilePath(this, uri)

                    // Latin Text Recognizer
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    // Feed the image to the recognizer
                    val result = recognizer.process(image)
                        // Raw scanned text
                        .addOnSuccessListener { scanned_text ->
                            // Raw data extraction
                            val raw_string = scanned_text.text

                            // Regex definitions
                            val distance_regex = Regex("""\b(?!\d{4})\d{3}|\b\d{1}[.]\d{2}""")
                            val time_regex     = Regex(""":[\dO]{2}:[\dO]{2}""")
                            val pace_regex     = Regex("""\b[\dO]:[\dO]{2}\b(?!\:)(?![ ]*[A-Za-z])""")
                            val speed_regex    = Regex("""\d{1,2}\.?\d*\s*k""")
                            val calories_regex = Regex("""\b(?!784\b)\d{2,4}\b""")

                            // Pattern search
                            val distance_res = distance_regex.find(raw_string)
                            val time_res     = time_regex.find(raw_string)
                            val pace_res     = pace_regex.find(raw_string)
                            val speed_res    = speed_regex.find(raw_string)
                            val calories_res = calories_regex.findAll(raw_string).lastOrNull() // lastOrNull grabs the very last value (the callories)

                            // Value extraction
                            val distance           = distance_res?.value
                            val time_post          = time_res?.value
                            val time               = time_post?.drop(1) // removes the ":" at the start
                            val pace               = pace_res?.value
                            val speed_post         = speed_res?.value
                            val speed              = speed_post?.dropLast(2) // removes the " k" from the end
                            val calories           = calories_res?.value

                            // UI update
                            val stats_map = mapOf(
                                R.id.tile_distance to distance,
                                R.id.tile_time     to time,
                                R.id.tile_pace     to pace,
                                R.id.tile_speed    to speed,
                                R.id.tile_calories to calories )

                            for ( (tile_id, value) in stats_map) {
                                val tile_view = findViewById<android.view.View>(tile_id)
                                val value_text_view = tile_view.findViewById<TextView>(R.id.tile_value)

                                // Set the text to the value, or "ERROR" if it failed
                                value_text_view.text = value ?: "ERROR"
                            }

                            // LOG TO NOTION button unlock + color change
                            val null_check = stats_map.containsValue(null)

                            if (null_check) {
                                // Button lock
                                log_press(true, log_btn)
                                Log.d("Lock", "Null detected, see 'Parser'") }
                            else {
                                // Button unlock
                                log_press(false, log_btn)
                            }

                            // Debugging shit
                            Log.d("MLKit", "Raw Scanned Text:\n${scanned_text.text}")
                            Log.d("Parser", "Found distance: $distance")
                            Log.d("Parser", "Found time: $time")
                            Log.d("Parser", "Found pace: $pace")
                            Log.d("Parser", "Found speed: $speed")
                            Log.d("Parser", "Found calories: $calories")
                            Log.d("Parser", "\n")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MLKit", "Scanner failed", e)
                        }


                } catch (e: Exception) { Log.e("PhotoPicker", "Failed to load image", e) }
            } else { Log.d("PhotoPicker", "No media selected") }
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
            // TODO: Notion logic
            Log.d("MainActivity", "Log to Notion button clicked")

            // Button lock
            log_press(true, log_btn)


            val feedback: TextView = findViewById(R.id.feedback)

            feedback.text = "Attempting to log..."
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