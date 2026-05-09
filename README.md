# Devoco - AI-Powered Study Assistant

Devoco is a modern Android application designed to transform static PDF documents into interactive study materials. By leveraging Google's **Gemini AI**, Devoco extracts content from your textbooks, research papers, or notes and generates comprehensive quizzes to help you master the material.

## 🚀 Features

-   **PDF-to-Quiz Transformation**: Automatically generate multiple-choice questions (MCQs) and theory questions from any PDF document.
-   **AI-Powered Insights**: Uses the Gemini API to analyze text and create context-aware questions with detailed explanations.
-   **Deep Integration**: Seamlessly import PDFs from other apps or your file manager.
-   **Local Storage**: Keep track of your documents and generated quizzes locally using Room database.
-   **Modern UI**: A clean, responsive interface built entirely with Jetpack Compose and Material 3.

## 🛠 Tech Stack

-   **Language**: Kotlin
-   **UI Framework**: Jetpack Compose
-   **Dependency Injection**: Hilt
-   **Database**: Room (for document and question persistence)
-   **Data Storage**: DataStore (for user preferences)
-   **AI Engine**: Google Gemini API (`generativeai` SDK)
-   **PDF Processing**: PDFBox-Android
-   **Architecture**: Clean Architecture with MVVM pattern

## 📱 Intent Filters: Seamless Integration

One of the core features of Devoco is its ability to integrate with the Android ecosystem through **Intent Filters**. Located in the `AndroidManifest.xml`, these filters allow the app to respond to actions from other applications.

### 1. Handling Shared Content (`ACTION_SEND`)
The app is registered to handle "share" actions. When you are in another app (like Chrome or a Drive viewer) and select "Share" on a PDF, Devoco appears as a destination.
```xml
<intent-filter>
    <action android:name="android.intent.action.SEND"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <data android:mimeType="application/pdf"/>
</intent-filter>
```

### 2. Opening Files (`ACTION_VIEW`)
Devoco can act as a PDF handler. If you click on a PDF file in your File Manager, you can choose to open it directly with Devoco to start generating your quiz immediately.
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <data android:mimeType="application/pdf"/>
</intent-filter>
```

### 3. App Entry (`ACTION_MAIN`)
Standard entry point that allows the app to be launched from the home screen launcher.

## 🧠 How it Works (Core Logic)

The heart of the application lies in its domain layer, specifically the `GenerateQuizUseCase`. 
1. **Extraction**: The app uses `PdfExtractor` (utilizing PDFBox) to convert the PDF pages into raw text.
2. **Prompt Engineering**: It constructs a precise prompt for the Gemini AI, instructing it to return a structured JSON response containing questions, options, and explanations.
3. **Parsing**: The JSON response is parsed into domain models and saved to the local database for offline access.

## 🛠 Setup

1.  Clone the repository.
2.  Obtain a **Gemini API Key** from the [Google AI Studio](https://aistudio.google.com/).
3.  Add your API key to the project (typically in a `local.properties` or as a constant, depending on your configuration).
4.  Build and run on an Android device or emulator.

---
*Developed by Chaos*
