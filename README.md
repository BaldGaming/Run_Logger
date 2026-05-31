# RunLogger — Workout OCR & Notion Sync

<img width="270" height="565.5" alt="RunLogger Interface" src="https://github.com/user-attachments/assets/8c792d04-a13c-4382-8ff1-86ba92d05a1f" />

## About The Project
RunLogger is a streamlined Android utility designed to eliminate the friction of manually logging workout data. By utilizing Optical Character Recognition (OCR), the app scans screenshots or photos of workout summaries (like treadmill displays or smartwatch screens), intelligently extracts the core performance metrics, and automatically pushes a structured log to a personal Notion database.

Developed as a hands-on technical deep dive, this project bridges modern Android development with third-party machine learning and RESTful API integrations.

## Core Features
* **Smart Image Scanning:** Leverages Google's ML Kit Vision API for fast, on-device text recognition.
* **Regex-Powered Extraction:** Employs precise regular expressions to sift through raw OCR text and accurately isolate variables like Distance, Time, Pace, Average Speed, and Calories.
* **Seamless Notion Integration:** Constructs a formatted JSON payload and pushes the data directly to a Notion database using the Notion API, tracking run types (e.g., Recovery, Speed) and perceived effort levels.
* **Modern Android UI:** Features edge-to-edge layout, native Android Photo Picker integration, and dynamic UI state management (disabling the log button until all data is verified).
* **Asynchronous Networking:** Uses Kotlin Coroutines and Retrofit to handle network calls safely in the background, preventing UI thread blocking.

## How It Works
1. **Select Media:** The user taps "Select Photo," triggering the secure Android Photo Picker to choose a workout summary image.
2. **ML Kit Processing:** The app passes the image to the TextRecognizer, which converts the visual data into raw text.
3. **Data Parsing:** Custom Regex patterns scan the raw string to identify and format the specific metrics, populating the UI tiles.
4. **Context Addition:** The user selects the *Run Type* and *Effort Level* via simple dropdown menus.
5. **Database Sync:** Hitting "Log to Notion" packages the data into a structured schema and posts it to the Notion API via Retrofit.

## Tech Stack
* **Language:** Kotlin
* **Framework:** Android SDK (API 24+)
* **Machine Learning:** Google ML Kit (Vision Text Recognition)
* **Networking:** Retrofit2, OkHttp (REST API integration)
* **Concurrency:** Kotlin Coroutines & Lifecycle Scopes
* **UI Components:** AndroidX, Material Design (AutoCompleteTextView)

## Developer Motivation
This project was built from scratch to solidify core software engineering concepts in a mobile environment. It served as a practical proving ground for mastering Kotlin syntax, understanding the Android Activity Lifecycle, implementing native device APIs, and managing asynchronous RESTful API requests.
