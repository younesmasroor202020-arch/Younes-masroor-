package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "director_notes")
data class DirectorNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sceneId: Int,
    val title: String,
    val note: String,
    val shotType: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_media")
data class SavedMediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sceneId: Int,
    val type: String, // "TTS", "MUSIC", "IMAGE"
    val title: String,
    val prompt: String,
    val audioPath: String? = null,
    val imageBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
