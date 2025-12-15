/*
Course: MAD204
Lab: Lab 5 – Media Favorites App
Name: Jennyfer Parmar
Student ID: A00201240
Date: 12/12/25

Description:
Main activity of the Media Favorites App.
Allows users to pick images or videos, display them,
save favorites using Room database, view them in a RecyclerView,
and export/import favorites using GSON.
*/
package com.example.lab5_jennyfer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab5_jennyfer.adapter.FavoritesAdapter
import com.example.lab5_jennyfer.data.FavoriteDatabase
import com.example.lab5_jennyfer.data.FavoriteMedia
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// MainActivity handles all UI interactions and app logic
class MainActivity : AppCompatActivity() {

    // Buttons used for user actions
    private lateinit var pickMediaButton: Button
    private lateinit var addFavoriteButton: Button
    private lateinit var exportButton: Button
    private lateinit var importButton: Button

    // Views used to display selected media
    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView

    // RecyclerView to display list of favorite media
    private lateinit var recyclerView: RecyclerView

    // Room database instance
    private lateinit var db: FavoriteDatabase

    // Adapter for RecyclerView
    private lateinit var adapter: FavoritesAdapter

    // SharedPreferences to store last opened media and exported JSON
    private val prefs by lazy { getSharedPreferences("prefs", MODE_PRIVATE) }

    // Gson instance for converting objects to/from JSON
    private val gson = Gson()

    // Holds URI of currently selected media
    private var selectedUri: Uri? = null

    // Holds type of selected media (image or video)
    private var selectedType: String? = null

    // Launcher for picking media from device storage
    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                handlePickedMedia(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge UI layout
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply padding to avoid overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Link UI components with XML views
        pickMediaButton = findViewById(R.id.pickMediaButton)
        addFavoriteButton = findViewById(R.id.addFavoriteButton)
        exportButton = findViewById(R.id.exportButton)
        importButton = findViewById(R.id.importButton)

        imageView = findViewById(R.id.imageView)
        videoView = findViewById(R.id.videoView)
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize Room database
        db = FavoriteDatabase.getInstance(this)

        // Setup RecyclerView adapter and delete callback
        adapter = FavoritesAdapter(mutableListOf()) { media, _ ->
            deleteWithUndo(media)
        }

        // Configure RecyclerView layout and adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Launch media picker when button is clicked
        pickMediaButton.setOnClickListener {
            pickMediaLauncher.launch("*/*")
        }

        // Add currently selected media to favorites
        addFavoriteButton.setOnClickListener {
            addSelectedToFavorites()
        }

        // Export favorites list to JSON
        exportButton.setOnClickListener {
            exportFavorites()
        }

        // Import favorites list from JSON
        importButton.setOnClickListener {
            importFavorites()
        }

        // Restore last opened media and reload favorites list
        loadLastMedia()
        reloadFavorites()
    }

    // Handle media selected from gallery
    private fun handlePickedMedia(uri: Uri) {
        selectedUri = uri
        selectedType = getMediaType(uri)

        // Display selected media
        displayMedia(uri, selectedType)

        // Save last opened media URI
        prefs.edit().putString("last_uri", uri.toString()).apply()
    }

    // Determine whether selected media is image or video
    private fun getMediaType(uri: Uri): String? {
        val mimeType = contentResolver.getType(uri) ?: return null
        return when {
            mimeType.startsWith("image") -> "image"
            mimeType.startsWith("video") -> "video"
            else -> null
        }
    }

    // Display selected media in ImageView or VideoView
    private fun displayMedia(uri: Uri, type: String?) {
        if (type == "image") {
            imageView.visibility = View.VISIBLE
            videoView.visibility = View.GONE
            imageView.setImageURI(uri)
        } else if (type == "video") {
            videoView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            videoView.setVideoURI(uri)
            videoView.start()
        }
    }

    // Save selected media into Room database
    private fun addSelectedToFavorites() {
        if (selectedUri == null || selectedType == null) {
            Snackbar.make(recyclerView, "Please pick media first", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Insert favorite media into database
        db.favoriteDao().insert(
            FavoriteMedia(
                uri = selectedUri.toString(),
                type = selectedType!!
            )
        )

        // Refresh RecyclerView
        reloadFavorites()
        Snackbar.make(recyclerView, "Added to favorites", Snackbar.LENGTH_SHORT).show()
    }

    // Load all favorites from database
    private fun reloadFavorites() {
        val list = db.favoriteDao().getAllFavorites()
        adapter.update(list)
    }

    // Delete a favorite item with undo option
    private fun deleteWithUndo(media: FavoriteMedia) {
        db.favoriteDao().delete(media)
        reloadFavorites()

        Snackbar.make(recyclerView, "Favorite deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                db.favoriteDao().insert(
                    FavoriteMedia(uri = media.uri, type = media.type)
                )
                reloadFavorites()
            }
            .show()
    }

    // Convert favorites list to JSON and save it
    private fun exportFavorites() {
        val favorites = db.favoriteDao().getAllFavorites()
        val json = gson.toJson(favorites)

        Log.d("JSON_EXPORT", json)
        prefs.edit().putString("export_json", json).apply()

        Snackbar.make(recyclerView, "Favorites exported (Logcat)", Snackbar.LENGTH_SHORT).show()
    }

    // Read JSON and insert favorites back into database
    private fun importFavorites() {
        val json = prefs.getString("export_json", null)

        if (json.isNullOrEmpty()) {
            Snackbar.make(recyclerView, "No JSON to import", Snackbar.LENGTH_SHORT).show()
            return
        }

        val typeToken = object : TypeToken<List<FavoriteMedia>>() {}.type
        val importedList: List<FavoriteMedia> = gson.fromJson(json, typeToken)

        for (item in importedList) {
            db.favoriteDao().insert(
                FavoriteMedia(uri = item.uri, type = item.type)
            )
        }

        reloadFavorites()
        Snackbar.make(recyclerView, "Favorites imported", Snackbar.LENGTH_SHORT).show()
    }

    // Restore last opened media when app restarts
    private fun loadLastMedia() {
        val uriString = prefs.getString("last_uri", null) ?: return
        val uri = Uri.parse(uriString)
        handlePickedMedia(uri)
    }
}
