package com.unipd.dei2026.simon

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipd.dei2026.simon.ui.theme.FontMatches
import com.unipd.dei2026.simon.ui.theme.FontText


//Nella schermata MatchHistory dispongo l'elenco delle partite giocate che vengono passate dal viewModel dopo essere state estratte dal
//database; viene gestita anche la rappresentazione della sequenza che fa riferimento a ciascuna partita
@Composable
fun MatchHistory(simonViewModel: SimonViewModel,  onPlayClicked:()->Unit, onMatchClicked:(Int,String)->Unit) {

    //importo l'immagine simongame1 nella directory res>drawable
    // Uso la classe Box come contenitore in cui inserire l'elemento Image in cui viene visualizzata la risorsa grafica
    val orientation=LocalConfiguration.current.orientation


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.simongame1),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            alpha = 0.28f,
        )


        Column(modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.Top) {
            //Gestisco il titolo della schermata tramite la funzione SimonBlinkText commentata sotto
            SimonBlinkText(text1="SIMON", text2="BLINK", configuration=orientation)

            //dal viewMOdel ricavo l'elenco delle partite giocate contenuto nella variabile correctedSeq (lista di stringhe) che viene
            //invertita con il metodo reversed() per visualizzare le partite appena giocate in cima alla lista
            val sequences = simonViewModel.correctedSeq.reversed()
            Spacer(modifier=Modifier.height(12.dp))
            //Inserisco gli elementi corrispondenti a ciascuna partita nel contenitore a scorrimento verticale,
            // Lazy Column permette di mostrare tutta la cronologia delle partite giocate in modo fluido
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(7.dp)
            ) {
                items(sequences) { sequence ->
                    if (sequence.isNotEmpty()) {
                        //da ciascun elemento della lista , divido in base agli spazi presenti e
                        //prendo  il primo elemento che contiene l'informazione riguardo all'indice del giocatore
                        //mentre il secondo elemento contiene i caratteri della sequenza corretta
                        val list = sequence.split(" ")
                        val playerIndex = list.getOrNull(0)?.toIntOrNull() ?: 0
                        val currentSeq = list.getOrNull(1) ?: ""

                        MatchRow(playerIndex=playerIndex, currentSeq=currentSeq, onClick={onMatchClicked(playerIndex, currentSeq)})
                    }

                }
            }

            //il FloatingActionButton conduce alla schermata di gioco
            FloatingActionButton(
                onClick = {
                    simonViewModel.resetGame()
                    onPlayClicked()
                },
                modifier = Modifier.padding(30.dp),
                containerColor = colorResource(R.color.glowCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Game"
                )
            }
        }
    }
}

//Nella funzione SimonBlinkText viene gestita esclusivamente il design del titolo della schermata iniziale
@Composable
fun SimonBlinkText(
    text1: String,
    text2: String,
    configuration:Int
){
    //creo una lista dei colori associati ai pulsanit che viene sfumata con il metodo horizontalGradient
    val rainbow = listOf(
        colorResource(R.color.magenta),
        colorResource(R.color.red),
        colorResource(R.color.yellow),
        colorResource(R.color.green),
        colorResource(R.color.cyan),
        colorResource(R.color.blue),
        colorResource(R.color.magenta)
    )
    val colorBack = Brush.horizontalGradient(rainbow)
    if (configuration==Configuration.ORIENTATION_PORTRAIT) {
        Text(modifier = Modifier.background(colorBack), text = text1,
            style = TextStyle(fontSize = 50.sp, fontFamily = FontMatches, drawStyle = Stroke(miter = 10f, width = 6f, join = StrokeJoin.Round),),
            color = colorResource(R.color.white),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(modifier = Modifier.background(colorBack), text = text2,
            style = TextStyle(fontSize = 50.sp, fontFamily = FontMatches, drawStyle = Stroke(miter = 10f, width = 6f, join = StrokeJoin.Round),),
            color = colorResource(R.color.white),
        )
    }else{
        Text(modifier = Modifier.background(colorBack), text = text1+ " "+ text2,
            style = TextStyle(fontSize = 50.sp, fontFamily = FontMatches, drawStyle = Stroke(miter = 10f, width = 6f, join = StrokeJoin.Round),),
            color = colorResource(R.color.white),
        )
    }
}

//Le funzioni StringSequenceShown e MatchRow mi consentono di rappresentare nella LazyColumn l'elenco delle partite giocate caratterizzato da:
//->punteggio o score_ ovvero il numero corrispondente alla sequenza massima realizzata dal giocatore prima di perdere
//->sequenza _ che è caratterizzata da: caratteri colorati , fino a dove il giocatore ha indovinato la sequenza
//                                      caratteri grigi, nel punto in cui il giocatore ha sbagliato o non ha cliccato nulla
@Composable
fun StringSequenceShown(
    score:Int,
    currentSeq:String ) : AnnotatedString {
    //Uso una funzione builder per creare una stringa speciale AnnotadedString , con cui posso fare modifiche a parti diverse di un testo
    return buildAnnotatedString {
        //uso la funzione forEachIndexed che mi permette di "scannerizzare" ogni singolo carattere di testo
        currentSeq.forEachIndexed { index, char ->
            //in base alla lettera determino il colore da usare
            val charColor = if (index < score) {
                when (char) {
                    'B' -> colorResource(R.color.blue)
                    'Y' -> colorResource(R.color.yellow)
                    'R' -> colorResource(R.color.red)
                    'C' -> colorResource(R.color.cyan)
                    'M' -> colorResource(R.color.magenta)
                    'G' -> colorResource(R.color.green)
                    else -> colorResource(R.color.white)
                }
            } else {
                colorResource(R.color.grey)
            }


            //con withStyle-> unisco lo stile al testo
            //in SpanStyle-> sono contenute le informazioni grafiche
            //con append-> aggiungo i caratteri alla singola stringa
            withStyle(style = SpanStyle(color = charColor)) {
                append(char)
            }
            //aggiungo la virgola tra un carattere e l'altro tranne nel caso dell'ultimo carattere
            if (index < currentSeq.lastIndex) {
                withStyle(style = SpanStyle(color = colorResource(R.color.grey))) {
                    append(", ")
                }
            }

        }

    }
}


@Composable
private fun MatchRow(
    playerIndex: Int,
    currentSeq: String,
    onClick: ()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable(onClick=onClick)
    ) {
        Text(
            modifier = Modifier
                .padding(2.dp)
                .background(
                    colorResource(R.color.light_white),
                    shape = RoundedCornerShape(4.dp)
                ),
            //se il giocatore preme End prima del turno successivo del computer
            // allora il punteggio corrisponde all'indice del giocatore che è uguale alla lunghezza della stringa corretta
            //se il giocatore perde o torna indietro allora il punteggio equivale alla lunghezza della stringa -1
            text = if (playerIndex==currentSeq.length) {"$playerIndex"}
            else{ val score=currentSeq.length-1
                "$score"},
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontText
            ),
            color = colorResource(R.color.white),
            maxLines = 1
        )
        Spacer(modifier=Modifier.width(10.dp))

        val textSeq =StringSequenceShown(score=playerIndex, currentSeq=currentSeq)

        Text(
            modifier = Modifier.padding(2.dp),
            text = textSeq,
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontText
            ),
            maxLines = 1,
            //uso il parametro successivo per troncare la stringa quando raggiunge il bordo dello schermo
            //e per aggiungere i tre puntini di sospensione (...) alla fine della sequenza
            overflow = TextOverflow.Ellipsis
        )
    }
}



