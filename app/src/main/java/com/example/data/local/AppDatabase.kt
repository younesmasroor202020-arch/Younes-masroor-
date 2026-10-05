package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectorDao {
    @Query("SELECT * FROM director_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<DirectorNoteEntity>>

    @Query("SELECT * FROM director_notes WHERE sceneId = :sceneId ORDER BY timestamp DESC")
    fun getNotesForScene(sceneId: Int): Flow<List<DirectorNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: DirectorNoteEntity): Long

    @Delete
    suspend fun deleteNote(note: DirectorNoteEntity)

    @Query("SELECT * FROM saved_media ORDER BY timestamp DESC")
    fun getAllMedia(): Flow<List<SavedMediaEntity>>

    @Query("SELECT * FROM saved_media WHERE type = :type ORDER BY timestamp DESC")
    fun getMediaByType(type: String): Flow<List<SavedMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: SavedMediaEntity): Long

    @Delete
    suspend fun deleteMedia(media: SavedMediaEntity)
}

@Database(entities = [DirectorNoteEntity::class, SavedMediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun directorDao(): DirectorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "roots_in_rawdah.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
