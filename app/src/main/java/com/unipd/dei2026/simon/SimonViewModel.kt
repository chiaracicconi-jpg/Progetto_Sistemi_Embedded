package com.unipd.dei2026.simon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import android.provider.Settings.Global.getString
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import kotlinx.coroutines.delay
import androidx.lifecycle.SavedStateHandle
import androidx.room.InvalidationTracker
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.Observer

class SimonViewModel(application:Application,
    private val state:SavedStateHandle):AndroidViewModel(application) {

    private val repository: GameRepository
    val allGames: LiveData<List<Game>>
    private val observer= Observer<List<Game>>{ gameList-> if (gameList!=null )
        { correctedSeq=gameList.map{it.game} } }
    val soundManager= SoundManager(application)

    var t : String by state.saveable{mutableStateOf("")}
    var startMatch: Boolean by state.saveable{mutableStateOf(false)}
    var isComputerTurn: Boolean by state.saveable{mutableStateOf(false)}
    var buttonTurnedOn: Char? by state.saveable{mutableStateOf(null)}

    var gameOver: Boolean by state.saveable{mutableStateOf(false)}
    var correctedSeq: List<String> by state.saveable{mutableStateOf(listOf())}
    var enabled: Boolean by state.saveable{mutableStateOf(true)}

    var pressed: Boolean by state.saveable{mutableStateOf(false)}
    var isPaused: Boolean by state.saveable{mutableStateOf(false)}
    var chooseLevel:Boolean by state.saveable { mutableStateOf(false) }
    var hardMode: Boolean by state.saveable { mutableStateOf(true) }


    private var computerSeq: String
        get()=state["computerSeq"] ?: ""
        set(value) {state["computerSeq"]=value}
    private var playerIndex: Int
        get()=state["playerIndex"] ?:0
        set(value) {state["playerIndex"]=value}
    private var computerIndex: Int
        get()=state["computerIndex"] ?: 0
        set(value) {state["computerIndex"]=value}

    private var computerJob:kotlinx.coroutines.Job?=null

    init{
        val gamesDao= GameRoomDatabase.getDatabase(application).gameDao()
        repository= GameRepository(gamesDao)
        allGames=repository.allGames

         allGames.observeForever(observer)

        if (startMatch && isComputerTurn && !gameOver) {
            computerTurn(newColor=false)}
    }
    fun insert(game:Game)=viewModelScope.launch(Dispatchers.IO){
        repository.insert(game)
    }
    override fun onCleared() {
        super.onCleared()
        soundManager.release()
        allGames.removeObserver(observer)
    }


    fun startGame(){
        if (startMatch==false){
            startMatch=true
            t=""
            computerSeq=""
            computerTurn()
        }
    }

    fun levelHard(isHard:Boolean){
        hardMode=isHard
    }

    fun chooseYourLevel(isChoosen:Boolean){
        chooseLevel=isChoosen
    }
    fun buttonEnabled(isEnabled:Boolean){
        enabled=isEnabled
    }


    fun buttonPauseEnabled() :Boolean {
        return  (isComputerTurn==true && !gameOver )
    }
    fun buttonPausePressed(){
        isPaused=!isPaused
    }

    private fun computerTurn(newColor: Boolean=true){
        computerJob?.cancel()
        computerJob= viewModelScope.launch{
                isComputerTurn=true
                playerIndex=0

                if (newColor) {
                    val colors = listOf('R', 'M', 'Y', 'G', 'C', 'B')
                    computerSeq += colors.random()
                    computerIndex=0
                }

                while(computerIndex<computerSeq.length){
                    val char= computerSeq[computerIndex]
                    if (isPaused) {
                        buttonTurnedOn = null

                        while (isPaused) {
                            delay(200)
                        }
                    }
                    if (!hardMode) {
                        delay(300)
                        buttonTurnedOn = char
                        delay(700)
                        buttonTurnedOn = null
                    }else{
                        delay(200)
                        buttonTurnedOn = char
                        delay(300)
                        buttonTurnedOn = null
                    }
                    computerIndex+=1
                }
            computerIndex=0
            isComputerTurn=false

        }
    }

    fun playerTurn(char:Char){
        if (!startMatch || isComputerTurn==true){
            return
        }
        if (char==computerSeq[playerIndex]){
            playerIndex+=1
            if (playerIndex==computerSeq.length){
                isComputerTurn=true
                viewModelScope.launch{
                    delay(800)
                    t=""
                    computerTurn()
                }
            }
        }
        else{
            gameOver=true
            startMatch=false

            val gamePlayed="$playerIndex $computerSeq"
            insert(Game(gamePlayed))
        }
    }
    fun endGame() {
        if (startMatch && !gameOver) {
            if (computerSeq.length == 1) {
                resetGame()
            } else {
                startMatch = false

                val char = "$playerIndex $computerSeq"
                insert(Game(char))

            }
        }
    }

    fun resetGame(){
        computerJob?.cancel()
        t=""
        startMatch=false
        isComputerTurn=false
        gameOver=false
        buttonTurnedOn=null
        computerSeq=""
        playerIndex=0
        enabled=true
        pressed=false
        isPaused=false
        computerIndex=0
        chooseLevel=false
    }

}