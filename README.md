# DeckCycle

A flashcard-based learning application designed to help users improve vocabulary retention and recall. The project consists of an Android application built with Kotlin and a backend powered by Firebase Firestore for cloud synchronization.

---
## Features

- User authentication via Firebase Authentication.
- Create, edit, and delete flashcard decks.
- Study modes:
  - Flip Mode: Classic flashcard flipping.
  - Quiz Mode: Multiple-choice questions.
  - Write Mode: Typing-based recall testing.
- Tracks study sessions, accuracy rates, and quiz performance.
- Cloud synchronization with Firebase Firestore.
- Push notifications as you login.

---
## Prerequisites

- Android Studio (latest version installed).
- Kotlin Plugin for Android development.
- Firebase Project Setup:
  - Enable Firebase Authentication and Firestore.
  - Add google-services.json to the app/ directory.

---
## Directory Structure
```bash
DeckCycle/
├── manifests/
│   ├── AndroidManifest.xml
├── java/com/example/deckcycle/
│   ├── model/       # Database and authentication models
│   ├── presenter/   # Business logic handlers
│   ├── view/        # UI Activities and Fragments
│   ├── util/        # Utility functions, adapters, and helpers
├── res/
│   ├── layout/      # XML files for UI components
│   ├── values/      # Strings, themes, and other resources
├── build.gradle     # Project build file
└── README.md        # Project documentation
```
---
## Installation and Usage

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/deckcycle.git
cd deckcycle

---

2. **Open the project in Android Studio and sync Gradle.**

---

3. **Set up Firebase:**:
 - Enable Firebase Authentication and Firestore.
 - Place google-services.json in the app/ directory.

---

4. **Run the application on an emulator or a physical Android device.**
