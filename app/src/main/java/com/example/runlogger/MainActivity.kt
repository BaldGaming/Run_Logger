package com.example.runlogger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Title at the start
        val title: android.widget.TextView = findViewById(R.id.my_title)
        title.text = "RUN LOGGER"

        // Photo upload button
        val select_photo: android.widget.Button = findViewById(R.id.upload_button)
        select_photo.text = "SELECT PHOTO"

        val feedback: android.widget.TextView = findViewById(R.id.feedback)
        feedback.text = "Ready to log"

        val notion_input: android.widget.EditText = findViewById(R.id.notion_input)
        select_photo.setOnClickListener {
            val user_data = notion_input.text.toString()
            android.util.Log.d("NOTION_TEST", "User wants to upload: $user_data")
        }

//        feedback.setOnClickListener {
//            feedback.text = "LMAO XD"
//        }
    }
}