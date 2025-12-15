/*
Course: MAD204
Lab: Lab 5 – Media Favorites App
Name: Jennyfer Parmar
Student ID: A00201240
Date: 12/12/25

Description:
Room database class that provides access
to the FavoriteMedia table.
*/

package com.example.lab5_jennyfer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Database annotation defines entities and database version
@Database(entities = [FavoriteMedia::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {

    // Provides access to DAO methods
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        // Volatile ensures visibility of changes across threads
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null

        // Returns a single instance of the database
        fun getInstance(context: Context): FavoriteDatabase {

            // Create database if it does not already exist
            return INSTANCE ?: synchronized(this) {

                // Build Room database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorites_db"
                )
                    // Allows database operations on main thread (acceptable for lab)
                    .allowMainThreadQueries()
                    .build()

                // Assign instance to singleton reference
                INSTANCE = instance

                // Return database instance
                instance
            }
        }
    }
}
