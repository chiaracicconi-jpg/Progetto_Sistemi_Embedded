package com.unipd.dei2026.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipd.dei2026.simon.ui.theme.DetailString
import com.unipd.dei2026.simon.ui.theme.FontText
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import com.unipd.dei2026.simon.ui.theme.FontKey


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