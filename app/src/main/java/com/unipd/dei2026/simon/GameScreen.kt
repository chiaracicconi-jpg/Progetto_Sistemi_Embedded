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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.unipd.dei2026.simon.ui.theme.FontButtons
import com.unipd.dei2026.simon.ui.theme.FontText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.unipd.dei2026.simon.ui.theme.FontMatches

@Composable
fun GameScreen( simonViewModel: SimonViewModel,
    onButtonClicked: ()-> Unit){

    //Definisco 4 variabili utili all'interno del codice
    // t: String -> contiene la sequenza corrente di valori corrispondenti ai pulsanti premuti (es: R, M, Y, G, C, B);
    //              la stringa termina dopo aver premuto uno dei due pulsanti: Cancella o Fine Partita;
    //              Cancella: tutti i valori vengono persi, t viene reinizializzata a t=""
    //              Fine Partita: tutti i valori vengono salvati nella stringa playedMatches, t viene reinizializzata a t=""
    // c:Int -> contiene il numero di pulsanti colorati premuti (quando non viene premuto nessun pulsante c=0)
    //              il conteggio termina dopo aver premuto uno dei due pulsanti: Cancella o Fine Partita
    //              Cancella: tutti i valori vengono persi, c viene reinizializzato a c=0
    //              Fine Partita: tutti i valori vengono salvati nella stringa playedMatches, c viene reinizializzato a c=0
    // orientation: Int ->  realizzata sulla base del codice dell'applicazione ManageOrientation (Composable);
    //                      in base alla modalità in cui si trova il telefono, Landscape o Portrait, cambia il layout
    // playedMatches: String -> contiene tutte le sequenze relative alle partite giocate (c-t);
    //                          è il parametro che viene passato dalla funzione onButtonClicked alla seconda schermata;
    //                          contiene "|" come parametro divisore tra una sequenza (1 partita) e un'altra;
    //                          non viene reinizializzato al click dei pulsanti Cancella o Fine Partita, ma una volta terminata l'applicazione

    val t =simonViewModel.t
    val orientation = LocalConfiguration.current.orientation

    BackHandler() {
        simonViewModel.endGame()
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
            //Uso una classe Row come contenitore in cui inserire i pulsanti colorati
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                //[commentato a partire dalla riga 171]
                CreateRows(
                    text = t,
                    textUpdated = { simonViewModel.t = it },
                    oriented = orientation,
                    viewModel= simonViewModel)
            }
            //sfrutto la variabile orientation per definire le condizioni in cui collocare la stringa di testo e i due pulsanti Cancella e Fine Partita
            // modalità Portrait -> sono centrati orizzontalmnete e spostati in basso;
            // modalità Landscape-> sono centrati verticalmente e spostati a destra;
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                //[commentato a partire dalla riga 257]
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
                    viewModel = simonViewModel
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

}



//creo una funzione compostable CreateRows che mi crea, in una colonna, 3 righe in cui inserire i bottoni;
//inserisco due disposizioni a seconda della modalità Landscape o Portrait:
//Portrait: i bottoni sono in alto al centro dello schermo
//Landscape: i bottoni sono a sinistra dello schermo (viene cambiata la struttura dei bottoni);
//la struttura e le caratteristiche di ciascun bottone sono definite nella funzione ColoredButton
@Composable
fun CreateRows( text: String, textUpdated:(String)->Unit, oriented: Int, viewModel: SimonViewModel){
    if (oriented==Configuration.ORIENTATION_PORTRAIT){
        Column(
            horizontalAlignment =Alignment.CenterHorizontally
        ){
            Spacer(modifier=Modifier.height(50.dp))
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.red, glowColor = R.color.glowRed, char='R', viewModel=viewModel )
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.magenta, glowColor = R.color.glowMagenta, char='M',  viewModel=viewModel)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.yellow, glowColor = R.color.glowYellow, char='Y', viewModel=viewModel)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.green, glowColor = R.color.glowGreen, char='G',  viewModel=viewModel)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.cyan, glowColor = R.color.glowCyan, char='C', viewModel=viewModel)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.blue, glowColor = R.color.glowBlue, char='B',  viewModel=viewModel)
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
                    .width(130.dp), stringChanged = textUpdated , color= R.color.red, glowColor = R.color.glowRed, char='R',  viewModel=viewModel)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.magenta, glowColor = R.color.glowMagenta, char='M', viewModel=viewModel)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.yellow, glowColor = R.color.glowYellow, char='Y',  viewModel=viewModel)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.green, glowColor = R.color.glowGreen, char='G',  viewModel=viewModel)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.cyan, glowColor = R.color.glowCyan, char='C',  viewModel=viewModel)
                ColoredButton(text=text,modifier=Modifier
                    .height(100.dp)
                    .width(130.dp), stringChanged = textUpdated , color= R.color.blue, glowColor = R.color.glowBlue, char='B',  viewModel=viewModel)
            }

        }
    }

}


//Nella funzione ColoredButton viene definito il valore di default del modifier;
//in modifier vengoni impostati i margini, larghezza e altezza e la funzione shadow utile
//per impostare l'elevation, ovvero la distanza tra la superficie di un elemento e lo sfondo


@Composable
fun ColoredButton(modifier:Modifier=Modifier,
                  char:Char,
                  color:Int,
                  glowColor:Int,
                  text:String,
                  stringChanged:(String)-> Unit,
                  viewModel: SimonViewModel
){
    val isGlowing=viewModel.buttonTurnedOn

    val clicked=remember{ MutableInteractionSource() }
    val isPressed by clicked.collectIsPressedAsState()
    LaunchedEffect(isPressed) {
        viewModel.buttonPressed(isPressed)
    }

    val colorButton=if ((char==isGlowing)||viewModel.pressed) glowColor else color

    Button(
        onClick = {
            if (viewModel.isComputerTurn) return@Button
            //al Click viene aggiornata la stringa di testo t
            val comma =if (text.isEmpty()) "" else ", "
            val textChanged=text+ comma+ char
            //uso delle funzioni di callback
            //stringChanged: aggiorna la variabile t
            //countChanged: aggiorna la variabile c
            stringChanged(textChanged)

            viewModel.playerTurn(char)

        },
        enabled=!viewModel.gameOver && viewModel.startMatch,
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
                    //questa funzionalità della classe Button,
                    //avvia al Click la partita e
                    // viene disattivato il pulsante successivamente
                    viewModel.startGame()
                },
                enabled=viewModel.enabled,
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
                    // Quando il pulsante è attivo ha il fondo bianco e la scritta viola scuro
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

            //nel Button di Fine Partita vado a salvare, aggiungendole in coda, le sequenze corrispondenti a ciascuna partita
            // tra una sequenza e l'altra inserisco un separatore "|";
            // "|" mi servirà nella schermata 2 per separare le singole sequenze
            Button(
                onClick = {viewModel.endGame()
                    nextActivity()
                          },
                enabled=!viewModel.gameOver,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.white),
                    contentColor = colorResource(R.color.violet),
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

