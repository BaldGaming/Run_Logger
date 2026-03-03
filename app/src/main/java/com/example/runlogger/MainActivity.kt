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

        // Android's photo picker
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
                            val distance_regex = Regex("""-\s*\d{2}:\d{2}\s*\n(.*)""")
                            val time_regex     = Regex("""[\dO]:[\dO]{2}:[\dO]{2}""")
                            val pace_regex     = Regex("""\b[\dO]:[\dO]{2}\b(?!\:)""")
                            val speed_regex    = Regex("""\d{1,2}\.?\d*\s*kph""")
                            val calories_regex = Regex("""\b\d{3}\b""")

                            // Pattern search
                            val distance_res = distance_regex.find(raw_string)
                            val time_res     = time_regex.find(raw_string)
                            val pace_res     = pace_regex.find(raw_string)
                            val speed_res    = speed_regex.find(raw_string)
                            val calories_res = calories_regex.find(raw_string)

                            // Value extraction
                            val distance = distance_res?.value
                            val time     = time_res?.value
                            val pace     = pace_res?.value
                            val speed    = speed_res?.value
                            val calories = calories_res?.value

                            // Debugging shit
                            Log.d("MLKit", "Raw Scanned Text:\n${scanned_text.text}")
                                    Log.d("Parser", "Found distance: $distance")
                            Log.d("Parser", "Found time: $time")
                            Log.d("Parser", "Found pace: $pace")
                            Log.d("Parser", "Found speed: $speed")
                            Log.d("Parser", "Found calories: $calories")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MLKit", "Scanner failed", e)
                        }

                    // debug shit
                } catch (e: Exception) { Log.e("PhotoPicker", "Failed to load image", e) }
            } else { Log.d("PhotoPicker", "No media selected") }
        }

        // "SELECT PHOTO" Button.
        val select_photo_btn: Button = findViewById(R.id.upload_button)
        select_photo_btn.setOnClickListener {
            Log.d("MainActivity", "Select Photo button clicked")
            // TODO: gallery logic
            // calls photo picker
            image_select.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        // "LOG TO NOTION" Button
        val log_btn: Button = findViewById(R.id.log_button)
        log_btn.setOnClickListener {
            Log.d("MainActivity", "Log to Notion button clicked")
            // TODO: Notion logic
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
}