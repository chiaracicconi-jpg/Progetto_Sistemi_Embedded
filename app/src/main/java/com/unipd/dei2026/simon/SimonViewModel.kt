package com.unipd.dei2026.simon

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.delay
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.Observer

//Nella classe ViewModel vado a definire la logica del gioco
//il viewModel mi consende di:
//->salvare lo stato dell'istanza, ovvero lo stato temporaneo della schermata,
//gestito con SavedStateHandle (che mi garantisce il salvataggio delle schermata se è terminata da Android per recuperare risorse)
//tutte le variabili legate al SavedStateHandle soppravvivono al cambio di configurazione (Portrait/Landscape) o al
//congelamento dell'app in backgruond
//-> salvare lo stato persistente, ovvero quello permanente , utilizzando Room Database (chiamando il Repository)
class SimonViewModel(application:Application,
    private val state:SavedStateHandle):AndroidViewModel(application) {

    //Definisco tutte le variabili utili all'interno del codice

    private val repository: GameRepository
    //punto di accesso al database
    val allGames: LiveData<List<Game>>
    //lista reattiva di tutte le partite salvate
    private val observer= Observer<List<Game>>{ gameList-> if (gameList!=null )
        { correctedSeq=gameList.map{it.game} } }
    //osservatore che collego ad allGames per estrarre il testo e aggiornare la variabile correctedSeq (più in basso)
    val soundManager= SoundManager(application)
    // inizializza SoundManager per la gestione dei suoni associati a ciascun pulsante

    var t : String by state.saveable{mutableStateOf("")}
    // t: String -> contiene la sequenza corrente di valori corrispondenti ai pulsanti premuti (es: R, M, Y, G, C, B);
    // viene usata temporaneamente e quindi reinizzializzata in startGame(), resetGame() e playerTurn()
    var startMatch: Boolean by state.saveable{mutableStateOf(false)}
    //gestisce lo stato della partita quando viene cliccato il pulsante Start (e successivamente viene selezionata la modalità di gioco)
    var isComputerTurn: Boolean by state.saveable{mutableStateOf(false)}
    //quando è true il computer riproduce la sequenza; utile per gestire l'attivazione e disattivazione
    //sia dei pulsanti colorati che dei pulsanti di gioco
    var buttonTurnedOn: Char? by state.saveable{mutableStateOf(null)}
    //è fondamentale per l'accensione e la corrispettiva riproduzione del suono associato di ciascun pulsante
    //contiene il carattere di quale pulsante è illuminato
    var gameOver: Boolean by state.saveable{mutableStateOf(false)}
    //nel momento il cui il giocatore commette un errore diventa true
    //il gioco si interrompe e i vari pulsanti si disattivano e viene mostrata la scritta Game Over
    var correctedSeq: List<String> by state.saveable{mutableStateOf(listOf())}
    //è la lista di stringhe contente tutte le partite passate che viene riempita dall'Observer e poi
    //viene passata a MatchHistory per mostrare la cronologia
    var enabled: Boolean by state.saveable{mutableStateOf(true)}
    //abilita o disabilita i pulsanti della UI in combinazione con la funzione buttonEnabled(Boolean)
    var pressed: Boolean by state.saveable{mutableStateOf(false)}
    //tiene traccia se un pulsante è stato premuto dal giocatore
    var isPaused: Boolean by state.saveable{mutableStateOf(false)}
    //in combinazione con le funzioni buttonPausedEnabled() e buttonPausedPressed()
    //il bottone Paused è abilitato quando è il turno del computer e disabilitato in tutti gli altri casi
    //se premuto blocca la riproduzione della sequenza finchè non viene ripremuto dal giocatore
    var chooseLevel:Boolean by state.saveable { mutableStateOf(false) }
    //in combinazione con la funzione chooseYourLevel(isChosen:Boolean)
    //è un flag booleano con cui controllo la visualizzazione sulla schermata del pop-up
    //con cui scelgo la modalità (Easy-Hard)
    var hardMode: Boolean by state.saveable { mutableStateOf(true) }
    //stabilisce la difficoltà della partita: true la sequenza del computer viene riprodotta molto velocemente
    // false la sequenza del computer viene riprodotta lentamente

    //Le prossime variabili servono esclusivamente per la logica del gioco
    private var computerSeq: String
        get()=state["computerSeq"] ?: ""
        set(value) {state["computerSeq"]=value}
    //contiene la sequenza corretta geenrata dal computer

    private var playerIndex: Int
        get()=state["playerIndex"] ?:0
        set(value) {state["playerIndex"]=value}
    //contiene i click del giocatore che viene riazzerato a ogni turno
    //e incrementato man a mano che il giocatore indovina la sequenza

    private var computerIndex: Int
        get()=state["computerIndex"] ?: 0
        set(value) {state["computerIndex"]=value}
    //contiene il conteggio del computer (utile per gestire i casi di interruzione
    //forzata dell'app)

    private var computerJob:kotlinx.coroutines.Job?=null
    //introtta a seguito di un bug che faceva mantere accesi i pulsanti e i suoni alla pressione
    //del pulsante End

    //Blocco di inizializzazione: eseguito una sola volta per configurare la connessione al database
    // e
    init{
        val gamesDao= GameRoomDatabase.getDatabase(application).gameDao()
        repository= GameRepository(gamesDao)
        allGames=repository.allGames

         allGames.observeForever(observer)
        //registra l'observer che va ad agdgiornare la lista delle partite

        if (startMatch && isComputerTurn && !gameOver) {
            computerTurn(newColor=false)}
        //gestisce la situazione in cui l'app viene interrotta facendo riprendere la sequenza del computer esattamente
        //da dove si era interrotta
    }

    //viene avviata una coroutineper salvare la partita giocata nel database
    fun insert(game:Game)=viewModelScope.launch(Dispatchers.IO){
        repository.insert(game)
    }

    //viene chiamata quando la schermata Android viene distrutta per "ripulire" il sistema, liberando il soundManager
    //(può provocare memory leak) e rimuovendo l'observer.
    override fun onCleared() {
        super.onCleared()
        soundManager.release()
        allGames.removeObserver(observer)
    }

    //la funzione starGame() serve per avviare il gioco e viene chiamata nel GameScreen alla pressione del pulsante Start
    //una  volta chiamata essa chiama a sua volta la funzione computerTurn()
    fun startGame(){
        if (startMatch==false){
            startMatch=true
            t=""
            computerSeq=""
            computerTurn()
        }
    }

    //la funzione levelHard() modifica la variabile booleana hardMode quando l'utente seleziona
    //la modalità "Hard" dal pulsante nella GameScreen
    fun levelHard(isHard:Boolean){
        hardMode=isHard
    }

    //la funzione chooseYourLevel() viene chiamata al click del pulsante "Start" per far apparire
    //la scelta della modalità sulla schermata GameScreen
    fun chooseYourLevel(isChoosen:Boolean){
        chooseLevel=isChoosen
    }

    //la funzione buttonEnabled() modifica la variabile enabled che viene utilizzata nel GameScreen
    //per disattivare il pulsante "Start" dopo l'inizio della partita
    fun buttonEnabled(isEnabled:Boolean){
        enabled=isEnabled
    }

    //la funzione buttonPausedEnabled() gestisce l'attivazione del pulsante "Pause" che si attiva
    // solo durante il turno del computer
    fun buttonPauseEnabled() :Boolean {
        return  (isComputerTurn==true && !gameOver )
    }
    //la funzione buttonPausedPressed() gestisce il click del pulsante "Paused" che se premuto mette in pausa
    //il turno del computer e si trasforma nel pulsante "Resume"
    fun buttonPausePressed(){
        isPaused=!isPaused
    }

    //la funzione computerTurn() gestisce il turno del computer:
    //- viene cancellato il turno precedente se è ancora in corso
    //- viene resettato il contatore del giocatore e la variabile isComputerTurn diventa true
    //- viene generato randomicamente un carattere da inserire nella sequenza del computer
    //- viene riprodotta visivamente la sequenza del computer (in base alla modalità scelta cambia la velocità di riproduzione)
    //- viene incrementato l'indice del computer di 1 man a mano
    //- viene passato il turno (l'indice del computer viene azzerato e isComputerTurn diventa false)
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

    //la funzione playerTurn() gestisce il turno del giocatore
    //- al click del pulsante viene passato il carattere corrispondente al bottone premuto
    //- se il carattere premuto dal giocatore corrisponde a quello nell'esatta posizione della sequenza del computer
    //l'indice del giocatore viene incrementato di 1
    //- se l'indice del giocatore e la lunghezza della sequenza del computer corrispondono allora il turno del giocatore termina
    //e avviene il passaggio al turno del computer
    //-altrimenti (ovvero se il giocatore sbaglia) la variabile gameOver diventa true, mentre la partita finisce (startmatch=false)
    //-la partita giocata viene inserita nel database
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

    //la funzione endGame() gestisce la fine del gioco ovvero
    //- Se al momento della pressione è in corso la presentazione della prima sequenza,
    // cioè quella di lunghezza 1, l’app si comporta come se la partita non fosse mai iniziata e alla lista non viene aggiunto alcun elemento
    //- Se viene premuto il pulsante "End" la partita termina e viene inserita la sequenza giocata nel database
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

    //la funzione resetGame() reinizializza le variabili alla fine di ogni partita
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