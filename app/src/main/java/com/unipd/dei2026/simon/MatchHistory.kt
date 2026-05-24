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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unipd.dei2026.simon.ui.theme.FontMatches
import com.unipd.dei2026.simon.ui.theme.FontText


@Composable
fun MatchHistory(simonViewModel: SimonViewModel, onPlayClicked:()->Unit, onMatchClicked:(Int,String)->Unit) {
    //Definisco 3 variabili utili all'interno del codice
    //matchesList:List<String> -> contiene una lista di stringhe ottenuta tramite la funzione split del parametro allMatches (passato da Activity1);
    //                            viene usata per la definizione delle due variabili successive sequences, counting
    //sequences:List<String> -> contiene una lista di stringhe che corrispondono alle sequenze di ciascuna partita giocata;
    //                          viene usata dalla funzione items di LazyColumn per inserire a ogni partita una nuova sequenza;
    //                          è creata dalla funzione filterIndex che "setaccia" la lista matchesList per salvare nella variabile sequences
    //                          quegli elementi della lista che hanno indice dispari
    //counting:List<String> -> contiene una lista di stringhe che corrispondono al conteggio dei bottoni premuti in ciascuna partita giocata;
    //                          viene usata dalla funzione items di LazyColumn per inserire a ogni partita un nuovo conteggio;
    //                          è creata dalla funzione filterIndex che "setaccia" la lista matchesList per salvare nella variabile counting
    //                          quegli elementi della lista che hanno indice pari

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

        //Uso una classe Column per creare un contenitore in cui inserire la stringa "Played Games"
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.Top) {

            SimonBlinkText(text1="SIMON", text2="BLINK", configuration=orientation)
            //Inserisco gli elementi corrispondenti a ciascuna partita nel contenitore a scorrimento verticale,
            // Lazy Column permette di mostrare tutta la cronologia delle partite giocate in modo fluido
            val sequences = simonViewModel.correctedSeq
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(7.dp)
            ) {
                items(sequences) { sequence ->
                    if (sequence.isNotEmpty()) {
                        val list = sequence.split(" ")
                        val playerIndex = list.getOrNull(0)?.toIntOrNull() ?: 0
                        val currentSeq = list.getOrNull(1) ?: ""

                        MatchRow(playerIndex=playerIndex, currentSeq=currentSeq, onClick={onMatchClicked(playerIndex, currentSeq)})
                    }

                }
            }


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
@Composable
fun SimonBlinkText(
    text1: String,
    text2: String,
    configuration:Int
){
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
@Composable
fun StringSequenceShown(
    score:Int,
    currentSeq:String ) : AnnotatedString {
    return buildAnnotatedString {
        //uso la funzione forEach che mi permette di "scannerizzare" ogni singolo carattere di testo
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
            .fillMaxSize()
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
            text = "$playerIndex",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontText
            ),
            color = colorResource(R.color.white),
            maxLines = 1
        )
        Spacer(modifier=Modifier.width(10.dp))
        //Uso una funzione builder per creare una stringa speciale AnnotadedString contenuta nella variabile
        //textSeq, con cui posso fare modifiche a parti diverse di un testo
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



