package com.unipd.dei2026.simon


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipd.dei2026.simon.ui.theme.DetailString
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.unipd.dei2026.simon.ui.theme.FontKey

//Per la schermata Dettaglio Partite ho utilizzato come prototipo quella di RecyclerSimple
//In ingresso ho 2 parametri:
//score:Int---> tiene conto del punteggio totalizzato dal giocatore durante ciascuna partita
//data:String---> tiene conto della sequenza corretta generata dal computer
@Composable
fun DetailScreen(
    score:Int,
    data:String
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            modifier = Modifier
                .padding(2.dp),
            text = if (score==data.length) {"$score"}
            else{ val index=data.length-1
                "$index"},
            style = TextStyle(
                fontSize = 80.sp,
                fontFamily = FontKey
            ),
            color = colorResource(R.color.white),
            maxLines = 1
        )
        Spacer(modifier=Modifier.height(20.dp))
        val textSeq =StringSequenceShown(score=score, currentSeq=data)

        Text(
            modifier = Modifier.padding(2.dp),
            text = textSeq,
            style = TextStyle(
                fontSize = 60.sp,
                fontFamily = DetailString
            ),
            softWrap = true,
        )
    }
}