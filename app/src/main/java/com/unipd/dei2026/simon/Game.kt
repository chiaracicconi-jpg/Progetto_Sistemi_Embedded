package com.unipd.dei2026.simon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="game_table")
data class Game(@PrimaryKey @ColumnInfo(name="game") val game:String )