package edu.quinnipiac.ser210.finalproject.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import edu.quinnipiac.ser210.finalproject.model.Game
import edu.quinnipiac.ser210.finalproject.model.GameDetails

@Entity(
    tableName = "predictions",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userOwnerId"]),
        ForeignKey(entity = GameDetails::class, parentColumns = ["gameId"], childColumns = ["gameId"])
    ],
    indices = [Index("userOwnerId"), Index("gameId")]
)
data class Prediction (
    @PrimaryKey(autoGenerate = true) val predictionId: Int = 0,
    val userOwnerId: Int,
    val gameId: String,
    val predictedWinner: String,
    val betType: String,
    val line: String,
    val bettingOdds: String,
    val betAmount: Int,
    val concluded:Boolean,
    val result:String


)