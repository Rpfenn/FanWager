package edu.quinnipiac.ser210.finalproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.quinnipiac.ser210.finalproject.model.GameDetails

@Database(
    entities = [GameDetails::class, Prediction::class, User::class],
    version = 2 // 🔼 Updated version to reflect schema changes
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao
    abstract fun predictionDao(): PredictionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fanwager_db"
                )
                    .fallbackToDestructiveMigration() // Optional: clears DB if migration is missing
                    .build().also { INSTANCE = it }
            }
        }
    }
}
