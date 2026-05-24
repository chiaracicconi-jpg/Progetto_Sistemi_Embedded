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
import kotlinx.coroutines.Dispatchers

class SimonViewModel(application:Application,
    private val state:SavedStateHandle):AndroidViewModel(application) {

    private val repository: GameRepository
    val allGames: LiveData<List<Game>>


    var t : String by state.saveable{mutableStateOf("")}
    var startMatch: Boolean by state.saveable{mutableStateOf(false)}
    var isComputerTurn: Boolean by state.saveable{mutableStateOf(false)}
    var buttonTurnedOn: Char? by state.saveable{mutableStateOf(null)}

    var gameOver: Boolean by state.saveable{mutableStateOf(false)}
    var correctedSeq: List<String> by state.saveable{mutableStateOf(listOf())}
    var enabled: Boolean by state.saveable{mutableStateOf(true)}

    var pressed: Boolean by state.saveable{mutableStateOf(false)}
    var isPaused: Boolean by state.saveable{mutableStateOf(false)}



    private var computerSeq: String
        get()=state["computer_seq"] ?: ""
        set(value) {state["computer_seq"]=value}
    private var playerIndex: Int
        get()=state["playerIndex"] ?:0
        set(value) {state["playerIndex"]=value}

    init{
        val gamesDao= GameRoomDatabase.getDatabase(application).gameDao()
        repository= GameRepository(gamesDao)
        allGames=repository.allGames

        allGames.observeForever { gameList->
            if (gameList!=null ){
                correctedSeq=gameList.map{it.game}
            }
        }

        if (startMatch && isComputerTurn && !gameOver && !isPaused) {
            computerTurn(newColor=false)}
        if (startMatch && !isComputerTurn && !gameOver) {
            enabled = true
        }
    }
    fun insert(game:Game)=viewModelScope.launch(Dispatchers.IO){
        repository.insert(game)
    }


    fun startGame(){
        if (startMatch==false){
            startMatch=true
            t=""
            computerSeq=""
            computerTurn()
        }
    }
    fun buttonPressed(isPressed:Boolean){
         pressed=isPressed
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
            viewModelScope.launch{
                isComputerTurn=true
                playerIndex=0

                if (newColor) {
                    val colors = listOf('R', 'M', 'Y', 'G', 'C', 'B')
                    computerSeq += colors.random()
                }

                for (char in computerSeq){
                    if (isPaused) {
                        buttonTurnedOn = null

                        while (isPaused) {
                            delay(200)
                        }
                    }

                    delay(300)
                    buttonTurnedOn=char
                    delay(700)
                    buttonTurnedOn=null
                }

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
                viewModelScope.launch{
                    delay(1000)
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

    }

}