package com.unipd.dei2026.simon


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Activity2(allMatches:String) {
    //Definisco 3 variabili utili all'interno del codice
        //matchesList:List<String> -> contiene una lista di stringhe ottenuta tramite la funzione split
        //                            viene usata nella definizione delle due variabili successive sequences, counting
        //sequences:List<String> -> contiene una lista di stringhe che corrispondono alle sequenze di ciascuna partita giocata
        //                          viene usata dalla funzione items di LazyColumn per inserire a ogni partita una nuova sequenza
        //                          creata dalla funzione filterIndex che "setaccia" la lista matchesList per salvare nella variabile sequences
        //                          quegli elementi della lista che hanno indice dispari
        //counting:List<String> -> contiene una lista di stringhe che corrispondono al conteggio dei bottoni premuti in ciascuna partita giocata
        //                          viene usata dalla funzione items di LazyColumn per inserire a ogni partita un nuovo conteggio
        //                          creata dalla funzione filterIndex che "setaccia" la lista matchesList per salvare nella variabile counting
        //                          quegli elementi della lista che hanno indice pari

    val matchesList = allMatches.split("|")
    val sequences = matchesList.filterIndexed { ind, _ -> ind % 2 != 0 }
    val counting = matchesList.filterIndexed { ind, _ -> ind % 2 == 0 }

    //importo l'immagine simongame1 nella directory res>drawable
    // Uso la classe Box come contenitore in cui inserire l'elemento Image in cui viene visualizzata la risorsa grafica

    Box(modifier = Modifier.fillMaxSize(),
       contentAlignment = Alignment.Center ) {
        Image(
            painter = painterResource(R.drawable.simongame1),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            alpha = 0.28f,
        )
    }
    Column(modifier= Modifier.fillMaxSize()){
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            Text(
                text = stringResource(R.string.played),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Thin),
                color = colorResource(R.color.white),
                maxLines = 1
            )
        }


        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {

            //Inserisco glie elementi corrispondenti a ciascuna partita nel contenitore a scorrimento verticale, Lazy Column
            //Permette di mostrare tutta la cronologia delle partite giocate in modo fluido
            LazyColumn(modifier = Modifier.weight(0.15f)) {
                items(counting) { count ->
                    Text(
                        modifier=Modifier.padding(2.dp)
                            .background(colorResource(R.color.light_white), shape = RoundedCornerShape(4.dp)),
                        text = count,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorResource(R.color.white),
                        maxLines = 1,
                    )

                }
            }

            LazyColumn(modifier = Modifier.weight(0.98f)) {
                items(sequences) { sequence ->

                    //Uso una funzione builder per creare una stringa speciale AnnotadedString
                    //con cui posso fare modifiche a parti diverse di un testo
                    val textSeq = buildAnnotatedString {
                        //uso la funzione forEach che mi permette di "scannerizzare" ogni singolo carattere di testo
                        sequence.forEach { char ->
                            //in base alla lettera determino il colore da usare
                            val charColor = when (char) {
                                'B' -> colorResource(R.color.blue)
                                'Y' -> colorResource(R.color.yellow)
                                'R' -> colorResource(R.color.red)
                                'C' -> colorResource(R.color.cyan)
                                'M' -> colorResource(R.color.magenta)
                                'G' -> colorResource(R.color.green)
                                else -> colorResource(R.color.white)
                            }

                            //con withStyle-> unisco lo stile al testo
                            //in SpanStyle-> sono contenute le informazioni grafiche
                            //con append-> aggiungo i caratteri alla stringa
                            withStyle(style = SpanStyle(color=charColor)) {
                                append(char)
                            }
                        }

                    }

                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = textSeq,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        //uso il parametro successivo per troncare
                        // la stringa quando raggiunge il bordo dello schermo
                        //e per aggiungere ... alla fine della sequenza
                        overflow = TextOverflow.Ellipsis
                    )

                }

            }
        }
    }
}