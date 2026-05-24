package com.unipd.dei2026.simon

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM game_table")
    fun getGameList(): LiveData<List<Game>>

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    fun insert(game:Game)
}