package com.unipd.dei2026.simon

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight



@Composable
fun Activity1( onButtonClicked: (String)-> Unit){

    //Definisco 4 variabili utili all'interno del codice
        // t: String -> contiene la sequenza corrente di valori corrispondenti ai pulsanti premuti (es: R, M, Y, G, C, B)
        //              la stringa termina dopo aver premuto uno dei due pulsanti: Cancella o Fine Partita
        //              Cancella: tutti i valori vengono persi, t viene reinizializzata a t=""
        //              Fine Partita: tutti i valori vengono salvati nella stringa playedMatches, t viene reinizializzata a t=""
        // c:Int -> contiene il numero di pulsanti colorati premuti (quando non viene premuto nessun pulsante c=0)
        //              il conteggio termina dopo aver premuto uno dei due pulsanti: Cancella o Fine Partita
        //              Cancella: tutti i valori vengono persi, c viene reinizializzato a c=0
        //              Fine Partita: tutti i valori vengono salvati nella stringa playedMatches, c viene reinizializzato a c=0
        // orientation: Int ->  realizzata sulla base del codice dell'applicazione ManageOrientation (Composable)
        //                      in base alla modalità in cui si trova il telefono, Landscape o Portrait, cambia il layout
        // playedMatches: String -> contiene tutte le sequenze relative alle partite giocate (c-t)
        //                          è il parametro che viene passato dalla funzione onButtonClicked alla seconda schermata
        //                          contiene "|" come parametro divisore tra una sequenza (1 partita) e un'altra
        //                          non viene reinizializzato al click dei pulsanti Cancella o Fine Partita, ma quando termina l'applicazione


    var t by rememberSaveable {mutableStateOf("")}
    var c by rememberSaveable {mutableStateOf(0)}
    var playedMatches by rememberSaveable {mutableStateOf("")}
    val orientation = LocalConfiguration.current.orientation

    //importo l'immagine simongame1 nella directory res>drawable
    // Uso la classe Box come contenitore in cui inserire l'elemento Image in cui viene visualizzata la risorsa grafica

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center ) {
        Image(
            painter = painterResource(R.drawable.simongame1),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            //uso l'istruzione alpha per rendere opaco il disegno in background
            alpha = 0.28f,
        )
    }

    //Uso una classe Column come contenitore in cui inserire i pulsanti colorati
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment =Alignment.CenterHorizontally
    ) {
        //[commentato a partire dalla riga 165]
        CreateRows(text = t, textUpdated = { t = it }, oriented = orientation, counting=c, countUpdate={c=it})
    }
    //Uso una classe Column come contenitore in cui inserire la stringa di testo e i pulsanti Cancella e Fine Partita
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),

        //sfrutto la variabile orientation per definire le condizioni in cui collocare la stringa di testo e i due pulsanti
        // modalità Portrait -> sono centrati in basso
        // modalità Landscape-> sono centrati a destra
        horizontalAlignment = if (orientation==Configuration.ORIENTATION_PORTRAIT){
            Alignment.CenterHorizontally }
            else{ Alignment.End
        },
        verticalArrangement = if (orientation==Configuration.ORIENTATION_PORTRAIT){
            Arrangement.Bottom
        }else{ Arrangement.Center}
    ) {
        Text(
            modifier = Modifier.width(260.dp).background(colorResource(R.color.light_black))

                //inserisco un modificatore per stabilire i limiti di altezza della mia barra di testo
                .heightIn(10.dp, 100.dp)
                //aggiungo una barra di scorrimento verticale per leggere il testo dopo che viene superato il limite massimo
                .verticalScroll(rememberScrollState()),
            text = t,
            color=colorResource(R.color.white),
            style = TextStyle(fontSize = 25.sp , fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
            //quando il testo arriva alla fine della riga va a capo automaticamente
            softWrap=true,


        )


        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                onClick = {
                    //funzionalità della classe Button:
                    //inizializza i valori della stringa e del conteggio al Click
                    t=""
                    c=0
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor=colorResource(R.color.light_white)),
                border = BorderStroke(1.dp, colorResource(R.color.white)),
                elevation= ButtonDefaults.buttonElevation(pressedElevation=5.dp)
            ) {
                Text(text = stringResource(R.string.delete),
                    style= TextStyle(fontSize=15.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium))
            }
            Spacer(modifier = Modifier.width(30.dp))

            //nel Button di Fine Partita vado a salvare, aggiungendole in coda, le sequenze corrispondenti a ciascuna partita
            // tra una sequenza e l'altra inserisco un separatore "|"
            // "|" mi servirà nella schermata 2 per separare le singole sequenze
            Button(
                onClick = {
                    //converto la variabile c: Int in String
                    // inserisco "|" anche tra il conteggio e l'elenco
                    //imposto le condizioni per le sequenze da aggiungere alla stringa complessiva
                    // se playedMatches è vuota, diventa la sequenza corrente
                    // se playedMatches non è vuota aggiungo in coda la sequenza corrente
                    // azzero il conteggio e la sequenza
                    val sequence= " $c |  "+ t
                    playedMatches=if (playedMatches.isEmpty()) {
                            sequence
                    }else {"$playedMatches|$sequence"}
                        onButtonClicked(playedMatches)

                        t=""
                        c=0
                    },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor=colorResource(R.color.light_white)),
                border = BorderStroke(1.dp, colorResource(R.color.white)),
                elevation= ButtonDefaults.buttonElevation(pressedElevation=5.dp)

            ) {
                Text(text = stringResource(R.string.endOf_game),
                    style= TextStyle(fontSize=15.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium))
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}



//creo una funzione compostable CreateRows che mi crea, in una colonna, 3 righe in cui inserire i bottoni
//inserisco due disposizioni a seconda della modalità Landscape o Portrait
//Portrait: i bottoni sono in alto al centro dello schermo
//Landscape: i bottoni sono a sinistra dello schermo (viene cambiata
//la struttura dei bottoni viene definita nella funzione ColoredButton
@Composable
fun CreateRows( text: String, textUpdated:(String)->Unit, oriented: Int, counting:Int, countUpdate:(Int)->Unit){
    if (oriented==Configuration.ORIENTATION_PORTRAIT){
        Column(
            horizontalAlignment =Alignment.CenterHorizontally
        ){
            Spacer(modifier=Modifier.height(50.dp))
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.red, char='R', countButton=counting, countChanged=countUpdate )
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.magenta, char='M', countButton=counting, countChanged=countUpdate)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.yellow, char='Y', countButton=counting, countChanged=countUpdate)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.green, char='G', countButton=counting, countChanged=countUpdate)
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.cyan, char='C', countButton=counting, countChanged=countUpdate)
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.blue, char='B', countButton=counting, countChanged=countUpdate)
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
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.red, char='R', countButton=counting, countChanged=countUpdate)
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.magenta, char='M', countButton=counting, countChanged=countUpdate)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.yellow, char='Y', countButton=counting, countChanged=countUpdate)
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.green, char='G', countButton=counting, countChanged=countUpdate)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.cyan, char='C', countButton=counting, countChanged=countUpdate)
                ColoredButton(text=text,modifier=Modifier.height(100.dp).width(130.dp), stringChanged = textUpdated , color= R.color.blue, char='B', countButton=counting, countChanged=countUpdate)
            }

        }
    }

}


//Nella funzione ColoredButton viene definito il valore di default del modifier
//in modifier vengoni impostati i margini, larghezza e altezza e la funzione shadow
//per impostare l'elevation, ovvero la distanza tra la superficie di un elemento e lo sfondo


@Composable
fun ColoredButton(modifier:Modifier=Modifier,
                  char:Char, color:Int,
                  text:String,
                  stringChanged:(String)-> Unit,
                  countButton:Int,
                  countChanged:(Int)->Unit){
    Button(
        onClick = {
            //al Click viene incrementato di 1 il punteggio e aggiornata la stringa di testo t
            val comma =if (text.isEmpty()) "" else ", "
            val textChanged=text+ comma+ char
            val plusOne=countButton+1

            //uso delle funzioni di callback
            //stringChanged: aggiorna la variabile t
            //countChanged: aggiorna la variabile c
            stringChanged(textChanged)
            countChanged(plusOne)
                  },
        colors = ButtonDefaults.buttonColors(containerColor=colorResource(color)),
        modifier = modifier.padding(2.dp).width(160.dp).height(130.dp)
            .shadow(elevation=12.dp,
                    shape=RoundedCornerShape(15.dp),
                    ambientColor = colorResource(R.color.black)),
        shape= RoundedCornerShape(15.dp)

    ) {}
}