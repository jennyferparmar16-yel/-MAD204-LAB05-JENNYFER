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

@Dao
interface FavoriteDao {

    @Insert
    fun insert(media: FavoriteMedia)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): List<FavoriteMedia>

    @Delete
    fun delete(media: FavoriteMedia)
}
