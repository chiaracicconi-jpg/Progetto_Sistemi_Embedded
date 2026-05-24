package com.unipd.dei2026.simon

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class GameRepository(private val gameDao: GameDao) {

    val allGames: LiveData<List<Game>>
        get()= gameDao.getGameList()

    @WorkerThread
    suspend fun insert(game:Game){
        gameDao.insert(game)
    }
}