package com.unipd.dei2026.simon

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.unipd.dei2026.simon.ui.theme.FontButtons
import com.unipd.dei2026.simon.ui.theme.FontText
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.unipd.dei2026.simon.ui.theme.FontMatches

//Nella schermata GameScreen vado a dispongo come viene visualizzato il gioco
//vengono pertanto realizzati i pulsanti colorati e quelli di gioco e viene collegato il funzionamento del gioco
//definito nel SimonViewModel
@Composable
fun GameScreen( simonViewModel: SimonViewModel,
    onButtonClicked: ()-> Unit){

    val t =simonViewModel.t
    val orientation = LocalConfiguration.current.orientation
    //la variabile orientation viene utilizzata per la disposizione degli elementi della schermata
    //sulla base della configurazione dello schermo (Portrait o Landscape);


    val soundManager=simonViewModel.soundManager
    //uso la variabile soundManager per implementare i metodi della classe SoundManager
    LaunchedEffect(simonViewModel.buttonTurnedOn) {
        val char=simonViewModel.buttonTurnedOn
        if (char!=null){
        soundManager.playSound(char)}
    }

    //gestisce l'azione eseguita quando l'utente preme il tasto back di sistema
    BackHandler() {
        simonViewModel.endGame()
        simonViewModel.resetGame()
        onButtonClicked()
    }

    //importo l'immagine simongame1 nella directory res>drawable
    // Uso la classe Box come contenitore in cui inserire l'elemento Image in cui viene visualizzata la risorsa grafica

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center ) {
        Image(
            painter = painterResource(R.drawable.simongame1),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            //uso l'istruzione alpha=Float per rendere opaco il disegno in background
            alpha = 0.28f,
        )
    }
    if (orientation== Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier=Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                CreateRows(
                    text = t,
                    textUpdated = { simonViewModel.t = it },
                    oriented = orientation,
                    viewModel= simonViewModel,
                    soundManager = soundManager)
            }
           Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {

                CreateStringButtons(
                    text = t,
                    nextActivity = onButtonClicked,
                    viewModel= simonViewModel
                )
            }
        }

    }else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f),
                verticalArrangement = Arrangement.Center
            ) {
                CreateRows(
                    text = t,
                    textUpdated = { simonViewModel.t = it },
                    oriented = orientation,
                    viewModel = simonViewModel,
                    soundManager = soundManager
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(15.dp)
                    .weight(0.5f)
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                CreateStringButtons(
                    text = t,
                    nextActivity = onButtonClicked,
                    viewModel = simonViewModel
                )
            }
        }
    }
    //Se la partita termina per un errore del giocatore appare al centro della schermata un pop-up contente la scritta
    // "Game Over" che non viene rimosso se non attraverso il tasto "Back" di sistema
    if (simonViewModel.gameOver) {
        Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .height(200.dp)
                    .width(340.dp)
                    .background(colorResource(R.color.light_black)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = "GAME",
                        color = colorResource(R.color.white),
                        style = TextStyle(
                            fontSize = 80.sp,
                            fontFamily = FontMatches,
                            drawStyle = Stroke(miter = 10f, width = 6f, join = StrokeJoin.Round),
                        )
                    )
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = "OVER",
                        color = colorResource(R.color.white),
                        style = TextStyle(
                            fontSize = 80.sp,
                            fontFamily = FontMatches,
                            drawStyle = Stroke(miter = 10f, width = 6f, join = StrokeJoin.Round),
                        )
                    )
                }
            }
        }
    }
    //una volta premuto il tasto "Start" appare sulla schermata un pop-up con il quale è possibile impostare la modalità del gioco
    //modalità Easy-> il gioco ha una velocità normale
    //modalità Hard-> il gioco ha una velocità maggiore
    if (simonViewModel.chooseLevel==true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .height(200.dp)
                    .width(290.dp)
                    .background(colorResource(R.color.light_black)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier=Modifier.padding(6.dp),
                        text=stringResource(R.string.chooseLevel),
                        color = colorResource(R.color.white),
                        style=TextStyle(fontSize = 25.sp, fontFamily = FontText )
                    )
                    Spacer(modifier=Modifier.height(15.dp))
                    Row(modifier=Modifier.padding(5.dp)) {
                        Button(
                            onClick={ simonViewModel.levelHard(false)
                                simonViewModel.chooseYourLevel(false)
                                    simonViewModel.startGame()},
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.buttonBlue), contentColor = colorResource(R.color.white)),
                            shape = RoundedCornerShape(5.dp),
                            border = BorderStroke(1.dp, colorResource(R.color.white))
                        ){
                            Text(modifier=Modifier.padding(2.dp),
                                text=stringResource(R.string.easy),
                                style=TextStyle(fontFamily = FontButtons)
                            )
                        }
                        Spacer(modifier=Modifier.width(10.dp))
                        Button(
                            onClick={ simonViewModel.levelHard(true)
                                simonViewModel.chooseYourLevel(false)
                                    simonViewModel.startGame()},
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.buttonRed), contentColor = colorResource(R.color.white)),
                            shape = RoundedCornerShape(5.dp),
                            border = BorderStroke(1.dp, colorResource(R.color.white))
                        ){
                            Text(modifier=Modifier.padding(2.dp),
                                text=stringResource(R.string.hard),
                                style=TextStyle(fontFamily = FontButtons)
                            )
                        }
                    }
                }
            }
        }
    }

}



//creo una funzione compostable CreateRows che mi crea, in una colonna, 3 righe in cui inserire i bottoni;
//inserisco due disposizioni a seconda della modalità Landscape o Portrait:
//Portrait: i bottoni sono in alto al centro dello schermo
//Landscape: i bottoni sono a sinistra dello schermo (viene cambiata la struttura dei bottoni);
//la struttura e le caratteristiche di ciascun bottone sono definite nella funzione ColoredButton
@Composable
fun CreateRows( text: String, textUpdated:(String)->Unit, oriented: Int, viewModel: SimonViewModel, soundManager: SoundManager){
    if (oriented==Configuration.ORIENTATION_PORTRAIT){
        Column(
            horizontalAlignment =Alignment.CenterHorizontally
        ){
            Spacer(modifier=Modifier.height(50.dp))
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.red, glowColor = R.color.glowRed, char='R', viewModel=viewModel, soundManager = soundManager )
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.magenta, glowColor = R.color.glowMagenta, char='M',  viewModel=viewModel, soundManager = soundManager)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.yellow, glowColor = R.color.glowYellow, char='Y', viewModel=viewModel, soundManager = soundManager)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.green, glowColor = R.color.glowGreen, char='G',  viewModel=viewModel, soundManager = soundManager)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.cyan, glowColor = R.color.glowCyan, char='C', viewModel=viewModel, soundManager = soundManager)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.blue, glowColor = R.color.glowBlue, char='B',  viewModel=viewModel, soundManager = soundManager)
            }
        }
    }
    else {
        Column(
            horizontalAlignment =Alignment.Start
        ){
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.red, glowColor = R.color.glowRed, char='R',  viewModel=viewModel, soundManager = soundManager)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.magenta, glowColor = R.color.glowMagenta, char='M', viewModel=viewModel, soundManager = soundManager)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.yellow, glowColor = R.color.glowYellow, char='Y',  viewModel=viewModel, soundManager = soundManager)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.green, glowColor = R.color.glowGreen, char='G',  viewModel=viewModel, soundManager = soundManager)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.cyan, glowColor = R.color.glowCyan, char='C',  viewModel=viewModel, soundManager = soundManager)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.blue, glowColor = R.color.glowBlue, char='B',  viewModel=viewModel, soundManager = soundManager)
            }

        }
    }

}


//Nella funzione ColoredButton viene definito il valore di default del modifier;
//-in modifier vengoni impostati i margini, larghezza e altezza e la funzione shadow utile
//-per impostare l'elevation, ovvero la distanza tra la superficie di un elemento e lo sfondo
//-la variabile isGlowing controlla il lampeggiare dei pulsanti (che devono illuminarsi sia quando viene riprodotta la sequenza
//del computer, sia quando il giocatore clicca il pulsante)
//-la stessa cosa viene gestita dal soundManager
@Composable
fun ColoredButton(modifier:Modifier=Modifier,
                  char:Char,
                  color:Int,
                  glowColor:Int,
                  text:String,
                  stringChanged:(String)-> Unit,
                  viewModel: SimonViewModel,
                  soundManager: SoundManager
){
    val isGlowing=viewModel.buttonTurnedOn

    val clicked=remember{ MutableInteractionSource() }
    val isPressed by clicked.collectIsPressedAsState()

    val colorButton=if ((char==isGlowing)||isPressed) glowColor else color

    Button(
        onClick = {
            if (viewModel.isComputerTurn) return@Button
            //al Click viene aggiornata la stringa di testo t
            val comma =if (text.isEmpty()) "" else ", "
            val textChanged=text+ comma+ char
            //uso delle funzioni di callback
            //stringChanged: aggiorna la variabile t
            stringChanged(textChanged)
            soundManager.playSound(char)
            viewModel.playerTurn(char)

        },
        // ciascun pulsante è attivo durante la partita
        enabled=!viewModel.gameOver && viewModel.startMatch,
        interactionSource = clicked,
        colors = ButtonDefaults.buttonColors(
            containerColor=colorResource(colorButton),
            disabledContainerColor =colorResource(colorButton) ),
        modifier = modifier
            .padding(2.dp)
            .width(160.dp)
            .height(130.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(15.dp),
                ambientColor = colorResource(R.color.black)
            ),
        shape= RoundedCornerShape(15.dp)

    ) {}
}

//Nella funzione CreateStringButton() vengono creati: la stringa di testoe i pulsanti di gioco "Start", "Pause", "Resume";
//1) nella stringa di testo viene visualizzata la sequenza riprodotta dal giocaotore
//2) il pulsante Start: -viene disattivato una volta iniziata la partita
//                      -permette la comparsa del pop-up con cui scegliere la modalità
//3) il pulsante Pause: -è attivo durante il turno del computer
//                      - se premuto si trasforma nel pulsante Resume interrompendo la riproduzione della sequenza
//4) il pulsante End : - è attivo solo durante la partita
//                     - se premuto fa terminare la partita e torna alla schermata iniziale

@Composable
fun CreateStringButtons(
    text:String,
    nextActivity: () -> Unit,
    viewModel: SimonViewModel){


    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val scrollState =rememberScrollState()
        LaunchedEffect(text) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
        Text(
            modifier = Modifier
                .width(280.dp)

                //inserisco un modificatore per stabilire i limiti di altezza della mia barra di testo
                .heightIn(10.dp, 100.dp)
                //aggiungo una barra di scorrimento verticale per leggere il testo dopo che viene superato il limite massimo
                .verticalScroll(scrollState),
            text = if(text.isEmpty() && viewModel.startMatch && !viewModel.isComputerTurn ) {
                stringResource(R.string.turn)
            }else{
                text},
            color = colorResource(R.color.white),
            style = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontText
            ),
            //quando il testo arriva alla fine della riga va a capo automaticamente
            softWrap = true,


            )

        Row (
            modifier= Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                onClick = {
                    viewModel.buttonEnabled(false)
                    //questa funzionalità della classe Button
                    //avvia al Click la partita e
                    // viene disattivato il pulsante successivamente
                    viewModel.chooseYourLevel(true)
                },
                enabled=!viewModel.startMatch && !viewModel.gameOver,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    // Quando il pulsante è attivo ha il fondo bianco e la scritta viola scuro
                    containerColor = colorResource(R.color.white),
                    contentColor= colorResource(R.color.violet),
                    // Quando il pulsante è disattivato ha fondo lilla e scritta bianca
                    disabledContainerColor = colorResource(R.color.light_white),
                    disabledContentColor = colorResource(R.color.white)

                ),
                border = BorderStroke(1.dp, colorResource(R.color.white)),
                elevation = ButtonDefaults.buttonElevation(pressedElevation = 5.dp)
            ) {
                Text(
                    text = stringResource(R.string.start),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontButtons
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick={
                    viewModel.buttonPausePressed()
                },
                enabled =viewModel.buttonPauseEnabled() ,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    // Quando il pulsante è attivo ha il fondo blu se non premuto, oppure rosso se premuto e la scritta bianca
                    containerColor = if (!viewModel.isPaused) {colorResource(R.color.buttonBlue)}
                    else{colorResource(R.color.buttonRed)},
                    contentColor= colorResource(R.color.white),
                    // Quando il pulsante è disattivato ha fondo lilla e scritta bianca
                    disabledContainerColor = colorResource(R.color.light_white),
                    disabledContentColor = colorResource(R.color.white)

                ),
                border = BorderStroke(1.dp, colorResource(R.color.white)),
                elevation = ButtonDefaults.buttonElevation(pressedElevation = 5.dp)
            ){
                Text(
                    text = if(!viewModel.buttonPauseEnabled()) {
                        stringResource(R.string.pause)
                    }else {if (!viewModel.isPaused) {stringResource(R.string.pause)}
                    else {stringResource(R.string.resume)}},
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontButtons
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            //nel pulsante "End" vado a richiamare le funzioni di endGame e resumeGame del viewModel e chiamo la funzione di callback
            //per passare alla schermata MatchHistory
            Button(
                onClick = {viewModel.endGame()
                    viewModel.resetGame()
                    nextActivity()
                          },
                enabled=viewModel.startMatch && !viewModel.gameOver,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    // Quando il pulsante è attivo ha il fondo bianco e la scritta viola scuro
                    containerColor = colorResource(R.color.white),
                    contentColor = colorResource(R.color.violet),
                    // Quando il pulsante è disattivato ha fondo lilla e scritta bianca
                    disabledContainerColor = colorResource(R.color.light_white),
                    disabledContentColor = colorResource(R.color.white)),
                border = BorderStroke(1.dp, colorResource(R.color.white)),
                elevation = ButtonDefaults.buttonElevation(pressedElevation = 5.dp)

            ) {
                Text(
                    text = stringResource(R.string.endOf_game),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontButtons
                    )
                )
            }
        }
    }

}


