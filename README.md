DeckCycle

Overview

DeckCycle is a flashcard-based learning application that helps users enhance their vocabulary and retention skills through multiple study modes. The app supports user authentication, deck management, and performance tracking to optimize the learning process.

Technologies Used

Programming Language: Kotlin

Database: SQLite (for local storage), Firebase Firestore (for cloud synchronization)

UI Framework: Android Jetpack

Networking: Firebase Authentication and Firestore

RecyclerView: For efficient list management in displaying decks and statistics

Notification System: Android Notification API

Directory Structure

DeckCycle/
│-- manifests/
│   ├── AndroidManifest.xml
│-- java/com/example/deckcycle/
│   ├── model/       # Database and user authentication models
│   ├── presenter/   # Business logic and interaction handlers
│   ├── view/        # UI Activities and Fragments
│   ├── util/        # Utility functions, adapters, and helpers
│-- res/
│   ├── layout/      # XML files for UI components
│   ├── values/      # Strings, themes, and other resources
│-- build.gradle

Features

User Authentication:

Registration and login using Firebase Authentication.

Deck Management:

Create, edit, and delete flashcard decks.

Store words in different decks.

Study Modes:

Flip Mode: Classic flashcard flipping.

Quiz Mode: Multiple-choice questions.

Write Mode: Typing-based recall testing.

Statistics Tracking:

Tracks study sessions, accuracy rates, time spent, and quiz performance.

Provides insights on learning progress.

Cloud Synchronization:

Upload and retrieve decks from Firebase Firestore.

Sync decks between multiple devices.

Push Notifications:

Reminders to study decks at scheduled intervals.

Prerequisites

Development Environment:

Android Studio (latest version)

Kotlin Plugin

Firebase configured in project

Dependencies:

Firebase Authentication and Firestore SDKs

Jetpack Components (RecyclerView, LiveData, ViewModel)

SQLite for local storage

Installation & Setup

Clone the repository:

git clone https://github.com/your-repo/deckcycle.git

Open in Android Studio and sync Gradle.

Set up Firebase:

Add google-services.json to app/ directory.

Enable Firebase Authentication and Firestore.

Run the application on an emulator or a physical device.
