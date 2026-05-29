package com.unipd.dei2026.simon

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData


//Gestisco il database prendendo come prototipo RoomWordsSample_start
// Nel mio database vado a salvare punteggio e sequenza di ciascuna partita giocata
//il Repository è il punto di connessione tra il Dao e il ViewModel che gestisce la logica del gioco
class GameRepository(private val gameDao: GameDao) {

    val allGames: LiveData<List<Game>>
        get()= gameDao.getGameList()

    //WorkerThread è un annotazione che garantisce la sicurezza dei thread
    //per scrivere i dati sulla memoria del telefono uso un thread di background

    @WorkerThread
    suspend fun insert(game:Game){
        gameDao.insert(game)
    }
}