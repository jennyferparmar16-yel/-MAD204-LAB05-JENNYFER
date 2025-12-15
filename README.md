# MAD204 – Lab 5: Media Favorites App

Name: Jennyfer Parmar  
Student ID: A00201240  
Course: MAD204  
Lab: Lab 5 – Media Favorites App  
Date: 12/12/25  

Description
This Android application is built using **Kotlin** and demonstrates media handling and local data persistence.  
Users can select images or videos from the device, view them in the app, and save them as favorites.  
Favorite media items are stored using a Room database and displayed in a RecyclerView.

The app also supports exporting and importing favorite items using GSON, restoring the last opened media using SharedPreferences, and provides an edge-to-edge UI experience.

Features
- Pick images or videos from device storage
- Display selected media (ImageView / VideoView)
- Save media as favorites using Room database
- View favorites in a RecyclerView
- Delete favorites with Snackbar UNDO option
- Export favorites to JSON using GSON
- Import favorites from JSON
- Restore last opened media using SharedPreferences
- Edge-to-edge UI support

Technologies Used
- Kotlin
- Android SDK
- Room Database
- RecyclerView
- GSON
- SharedPreferences
- Material Design Components

Project Structure
- `MainActivity.kt` – Main application logic
- `FavoriteMedia.kt` – Room entity
- `FavoriteDao.kt` – DAO interface
- `FavoriteDatabase.kt` – Room database
- `FavoritesAdapter.kt` – RecyclerView adapter
- `activity_main.xml` – Main UI layout
- `activity_item_favorite.xml` – RecyclerView item layout

How to Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device (API 24+)

Notes
This project was developed as part of MAD204 – Mobile Application Development coursework.
