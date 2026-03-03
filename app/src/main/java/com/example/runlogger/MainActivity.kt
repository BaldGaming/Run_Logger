package com.example.runlogger

// Libraries
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Main Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initializes the Dropdowns and label fields
        setup_dropdowns()
        setup_tiles()

        // "SELECT PHOTO" Button
        val select_photo_btn: Button = findViewById(R.id.upload_button)
        select_photo_btn.setOnClickListener {
            // TODO: gallery logic
            android.util.Log.d("RUN_LOGGER", "Select Photo clicked!")
        }

        // "LOG TO NOTION" Button
        val log_btn: Button = findViewById(R.id.log_button)
        log_btn.setOnClickListener {
            // TODO: Notion logic
            val feedback: TextView = findViewById(R.id.feedback)
            feedback.text = "Attempting to log..."
        }
    }

    // Function that handles the dropdown logic
    private fun setup_dropdowns() {
        val run_types = arrayOf("Recovery", "Speed", "Distance", "Running Machine")
        val effort_levels = arrayOf("Low", "Medium", "High")

        val type_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, run_types)
        val effort_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, effort_levels)

        val type_dropdown: AutoCompleteTextView = findViewById(R.id.dropdown_run_type)
        val effort_dropdown: AutoCompleteTextView = findViewById(R.id.dropdown_effort)

        type_dropdown.setAdapter(type_adapter)
        effort_dropdown.setAdapter(effort_adapter)
    }

    private fun setup_tiles() {
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