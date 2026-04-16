package com.unipd.dei2026.simon

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun Activity1( onButtonClicked: ()-> Unit){
    var t by rememberSaveable {mutableStateOf("")}
    val orientation = LocalConfiguration.current.orientation
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment =Alignment.CenterHorizontally
    ) {
        CreateRows(text = t, textUpdated = { t = it }, oriented = orientation)
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
            modifier = Modifier.width(300.dp).background(colorResource(R.color.grey)),
            text = t,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            softWrap=true

        )


        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                onClick = {
                    val cancel = if (t.isNotEmpty() == true) "" else ""
                    t = cancel
                },
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.delete))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = onButtonClicked,
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.endOf_game))
            }
        }

    }


}



@Composable
fun CreateRows( text: String, textUpdated:(String)->Unit, oriented: Int){
    if (oriented==Configuration.ORIENTATION_PORTRAIT){
        Column(
            horizontalAlignment =Alignment.CenterHorizontally
        ){
            Spacer(modifier=Modifier.height(50.dp))
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.red, char='R')
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.magenta, char='M')
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.yellow, char='Y')
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.green, char='G')
            }
            Row  {
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.cyan, char='C')
                ColoredButton(text=text, stringChanged = textUpdated , color= R.color.blue, char='B')
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
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.red, char='R')
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.magenta, char='M')
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.yellow, char='Y')
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.green, char='G')
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.cyan, char='C')
                ColoredButton(text=text,modifier=Modifier.height(90.dp).width(120.dp), stringChanged = textUpdated , color= R.color.blue, char='B')
            }

        }
    }

}

@Composable
fun ColoredButton(modifier:Modifier=Modifier,
                  char:Char, color:Int,
                  text:String,
                  stringChanged:(String)-> Unit){
    Button(
        onClick = {
            val comma =if (text.isEmpty()) "" else ", "
            val textChanged=text+ comma+ char
                 stringChanged(textChanged) },
        colors = ButtonDefaults.buttonColors(containerColor=colorResource(color)),
        modifier = modifier.padding(2.dp).width(155.dp).height(120.dp),
        shape= RoundedCornerShape(15.dp)
    ) {}
}