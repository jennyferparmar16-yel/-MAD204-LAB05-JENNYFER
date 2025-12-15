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


class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var pickMediaButton: Button
    private lateinit var addFavoriteButton: Button
    private lateinit var exportButton: Button
    private lateinit var importButton: Button

    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView
    private lateinit var recyclerView: RecyclerView

    // Database and adapter
    private lateinit var db: FavoriteDatabase
    private lateinit var adapter: FavoritesAdapter

    // SharedPreferences and JSON helper
    private val prefs by lazy { getSharedPreferences("prefs", MODE_PRIVATE) }
    private val gson = Gson()

    // Currently selected media
    private var selectedUri: Uri? = null
    private var selectedType: String? = null // image or video

    // Media picker (single selection)
    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                handlePickedMedia(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply window insets for system bars
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

        // Initialize views
        pickMediaButton = findViewById(R.id.pickMediaButton)
        addFavoriteButton = findViewById(R.id.addFavoriteButton)
        exportButton = findViewById(R.id.exportButton)
        importButton = findViewById(R.id.importButton)

        imageView = findViewById(R.id.imageView)
        videoView = findViewById(R.id.videoView)
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize Room database
        db = FavoriteDatabase.getInstance(this)

        // Setup RecyclerView
        adapter = FavoritesAdapter(mutableListOf()) { media, _ ->
            deleteWithUndo(media)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Button actions
        pickMediaButton.setOnClickListener {
            pickMediaLauncher.launch("*/*")
        }

        addFavoriteButton.setOnClickListener {
            addSelectedToFavorites()
        }

        exportButton.setOnClickListener {
            exportFavorites()
        }

        importButton.setOnClickListener {
            importFavorites()
        }

        // Load last opened media and favorites
        loadLastMedia()
        reloadFavorites()
    }

    /*
    Handle media picked from gallery
    */
    private fun handlePickedMedia(uri: Uri) {
        selectedUri = uri
        selectedType = getMediaType(uri)

        displayMedia(uri, selectedType)

        // Save last opened media
        prefs.edit().putString("last_uri", uri.toString()).apply()
    }

    /*
    Determine if the selected media is image or video
    */
    private fun getMediaType(uri: Uri): String? {
        val mimeType = contentResolver.getType(uri) ?: return null
        return when {
            mimeType.startsWith("image") -> "image"
            mimeType.startsWith("video") -> "video"
            else -> null
        }
    }

    /*
    Display image or video based on type
    */
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

    /*
    Add selected media to favorites database
    */
    private fun addSelectedToFavorites() {
        if (selectedUri == null || selectedType == null) {
            Snackbar.make(recyclerView, "Please pick media first", Snackbar.LENGTH_SHORT).show()
            return
        }

        db.favoriteDao().insert(
            FavoriteMedia(
                uri = selectedUri.toString(),
                type = selectedType!!
            )
        )

        reloadFavorites()
        Snackbar.make(recyclerView, "Added to favorites", Snackbar.LENGTH_SHORT).show()
    }

    /*
    Reload favorites from database into RecyclerView
    */
    private fun reloadFavorites() {
        val list = db.favoriteDao().getAllFavorites()
        adapter.update(list)
    }

    /*
    Delete favorite with Snackbar UNDO option
    */
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

    /*
    Export favorites to JSON using GSON
    */
    private fun exportFavorites() {
        val favorites = db.favoriteDao().getAllFavorites()
        val json = gson.toJson(favorites)

        Log.d("JSON_EXPORT", json)
        prefs.edit().putString("export_json", json).apply()

        Snackbar.make(recyclerView, "Favorites exported (Logcat)", Snackbar.LENGTH_SHORT).show()
    }

    /*
    Import favorites from JSON and save to database
    */
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

    /*
    Load last opened media on app restart
    */
    private fun loadLastMedia() {
        val uriString = prefs.getString("last_uri", null) ?: return
        val uri = Uri.parse(uriString)
        handlePickedMedia(uri)
    }
}
