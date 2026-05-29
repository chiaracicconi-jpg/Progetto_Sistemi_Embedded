package com.unipd.dei2026.simon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//Gestisco il database prendendo come prototipo RoomWordsSample_start
// Nel mio database vado a salvare punteggio e sequenza di ciascuna partita giocata
// uso version 2 perchè ho modificato la struttura delle tabelle (per il bug spiegato nel file Game.kt)
@Database(entities = [Game::class], version=2)
abstract class GameRoomDatabase: RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        private var INSTANCE: GameRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): GameRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameRoomDatabase::class.java,
                    "game_database"
                )
                    // se cambia la versione, o c'è un conflitto, il vecchio database viene
                    //cancellato e ne viene creato uno nuovo
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}