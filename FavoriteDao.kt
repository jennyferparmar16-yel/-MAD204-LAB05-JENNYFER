/*
Course: MAD204
Lab: Lab 5 – Media Favorites App
Name: Jennyfer Parmar
Student ID: A00201240
Date: 12/12/25

Description:
DAO interface that defines database operations
for FavoriteMedia.
*/

package com.example.lab5_jennyfer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// DAO annotation tells Room this interface handles database operations
@Dao
interface FavoriteDao {

    // Insert a FavoriteMedia object into the database
    @Insert
    fun insert(media: FavoriteMedia)

    // Retrieve all favorite media records from the database
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): List<FavoriteMedia>

    // Delete a specific FavoriteMedia record from the database
    @Delete
    fun delete(media: FavoriteMedia)
}
