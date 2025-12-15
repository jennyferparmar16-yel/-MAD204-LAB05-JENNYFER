/*
Course: MAD204
Lab: Lab 5 – Media Favorites App
Name: Jennyfer Parmar
Student ID: A00201240
Date: 12/12/25

Description:
Activity used to display the layout for a single
favorite media item. This activity applies edge-to-edge
layout handling for the item_favorite screen.
*/

package com.example.lab5_jennyfer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Activity class for item_favorite layout
class item_favorite : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Set the layout for this activity
        setContentView(R.layout.activity_item_favorite)

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
    }
}
