package com.unipd.dei2026.simon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Gestisco il database prendendo come prototipo RoomWordsSample_start
// Nel mio database vado a salvare punteggio e sequenza di ciascuna partita giocata
//in modo tale che vengano salvate le partite giocate alla terminazione dell'app e
//i dati vengano scritti nella memoria di archiviazione
@Entity(tableName="game_table") //sarà il nome della tabella nel mio database
data class Game( @ColumnInfo(name="game") val game:String,
    //per risolvere un bug individuato nel gioco, ovvero nel caso in cui ci fossero state due partite completamente identiche
    // pur avendo impostato nel Dao OnConflictStrategy.IGNORE, tuttavia il database si confondeva e la seconda partita non veniva salvata
    //in questo modo inserisco come identificatore unico l'id di ciascuna sequenza, che viene generato assegnando automaticamente (autoGenerate=true)
    // un numero progressivo per ogni dato salvato
                 @PrimaryKey(autoGenerate = true) val id: Int=0
)