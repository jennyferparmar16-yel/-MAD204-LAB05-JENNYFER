/*
Course: MAD204
Lab: Lab 5 – Media Favorites App
Name: Jennyfer Parmar
Student ID: A00201240
Date: 12/12/25

Description:
Entity class representing a favorite media item
stored in the Room database.
*/

package com.example.lab5_jennyfer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity annotation creates a database table named "favorites"
@Entity(tableName = "favorites")
data class FavoriteMedia(

    // Primary key for the favorites table
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Stores the URI of the selected media
    val uri: String,

    // Stores the type of media: image or video
    val type: String
)
