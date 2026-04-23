package com.unipd.dei2026.simon

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun Activity1( onButtonClicked: (String)-> Unit){
    var t by rememberSaveable {mutableStateOf("")}
    var c by rememberSaveable {mutableStateOf(0)}
    var playedMatches by rememberSaveable {mutableStateOf("")}
    val orientation = LocalConfiguration.current.orientation
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment =Alignment.CenterHorizontally
    ) {
        CreateRows(text = t, textUpdated = { t = it }, oriented = orientation, counting=c, countUpdate={c=it})
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = if (orientation==Configuration.ORIENTATION_PORTRAIT){
            Alignment.CenterHorizontally }
            else{ Alignment.End
        },
        verticalArrangement = if (orientation==Configuration.ORIENTATION_PORTRAIT){
            Arrangement.Bottom
        }else{ Arrangement.Center}
    ) {
        Text(
            modifier = Modifier.width(300.dp).background(colorResource(R.color.light_black))
                .heightIn(10.dp, 100.dp)
                .verticalScroll(rememberScrollState()),
            text = t,
            color=colorResource(R.color.white),
            style = TextStyle(fontSize = 25.sp , fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
            softWrap=true,


        )


        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                onClick = {
                    t=""
                    c=0
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor=colorResource(R.color.light_violet))
            ) {
                Text(text = stringResource(R.string.delete),
                    style= TextStyle(fontSize=15.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium))
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(
                onClick = {
                    val sequence= " $c |  "+ t
                    playedMatches=if (playedMatches.isEmpty()) {
                            sequence
                    }else {"$playedMatches|$sequence"}
                        onButtonClicked(playedMatches)

                        t=""
                        c=0
                    },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor=colorResource(R.color.light_violet))

            ) {
                Text(text = stringResource(R.string.endOf_game),
                    style= TextStyle(fontSize=15.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium))
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}



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

@Composable
fun ColoredButton(modifier:Modifier=Modifier,
                  char:Char, color:Int,
                  text:String,
                  stringChanged:(String)-> Unit,
                  countButton:Int,
                  countChanged:(Int)->Unit){
    Button(
        onClick = {
            val comma =if (text.isEmpty()) "" else ", "
            val textChanged=text+ comma+ char
            val plusOne=countButton+1

            stringChanged(textChanged)
            countChanged(plusOne)
                  },
        colors = ButtonDefaults.buttonColors(containerColor=colorResource(color)),
        modifier = modifier.padding(2.dp).width(160.dp).height(130.dp),
        shape= RoundedCornerShape(15.dp)
    ) {}
}