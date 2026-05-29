package com.unipd.dei2026.simon

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//Gestisco il database prendendo come prototipo RoomWordsSample_start
// Nel mio database vado a salvare punteggio e sequenza di ciascuna partita giocata
@Dao
interface GameDao {
    //scrivo la funzione che mi permette di leggere i dati in modo reattivo
    @Query("SELECT * FROM game_table")
    fun getGameList(): LiveData<List<Game>>

    //scrivo la funzione che mi permette di inserire i dati nel database
    @Insert(onConflict= OnConflictStrategy.IGNORE)
    fun insert(game:Game)
}