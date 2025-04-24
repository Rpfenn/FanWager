package edu.quinnipiac.ser210.finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.quinnipiac.ser210.finalproject.model.Game
import edu.quinnipiac.ser210.finalproject.model.GameDetails
@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: GameDetails): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameDetails>)

    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<GameDetails>



}