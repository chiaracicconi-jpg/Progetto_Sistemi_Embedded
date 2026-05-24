package com.unipd.dei2026.simon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

class SimonViewModel(application:Application):AndroidViewModel(application) {
    var t by mutableStateOf("")
    var startMatch by mutableStateOf(false)
    var isComputerTurn by mutableStateOf(false)
    var buttonTurnedOn by mutableStateOf<Char?>(null)
        private set
    var gameOver by mutableStateOf(false)
    var correctedSeq by mutableStateOf(listOf<String>())
    var enabled by mutableStateOf(true)

    var pressed by mutableStateOf(false)
    var isPaused by mutableStateOf(false)



    private var computerSeq=""
    private var playerIndex=0


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

    private fun computerTurn(){
            viewModelScope.launch{
                isComputerTurn=true
                playerIndex=0

                val colors=listOf('R', 'M', 'Y', 'G', 'C', 'B')
                computerSeq+=colors.random()

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
            correctedSeq=correctedSeq+gamePlayed
        }
    }
    fun endGame() {
        if (startMatch && !gameOver) {
            if (computerSeq.length == 1) {
                resetGame()
            } else {
                startMatch = false

                val char = "$playerIndex $computerSeq"
                correctedSeq = correctedSeq + char

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