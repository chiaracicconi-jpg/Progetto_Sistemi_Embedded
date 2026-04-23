package com.unipd.dei2026.simon

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unipd.dei2026.simon.ui.theme.SimonTheme

// La class Main Activity viene costruita prendendo come modello l'applicazione Secret Message
//Inserisco al suo interno le due schermate Activity1 e Activity2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SimonTheme {
                val navController = rememberNavController()

                // Creo lo Scaffold che costituirà l'impalcatura del mio codice,
                // Vado a impostare il colore del background

                Scaffold(modifier = Modifier.fillMaxSize()

                                    // nel metodo  verticalGradient() creo la lista contenti i colori che andrò a sfumare(viola_chiaro, viola)
                                    .background(brush = Brush.verticalGradient(colors=listOf(Color(0xFF7B50A4), Color(0xF32E1745)))),

                        //imposto lo sfondo trasparente, altrimenti otterrò uno sfondo bianco
                        containerColor = Color.Transparent) { innerPadding ->

                    NavHost(
                        navController = navController, startDestination = "activity1",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("activity1") {
                            Activity1(

                                // definisco la funzione di callback per il composable Activity1, onButtonClicked
                                //onButtonClicked passa come parametro una stringa contente l'elenco delle partite giocate (gestito in Activity2)
                                // Importo la classe Uri dalla libreria Android (import android.net.Uri) che gestisce l'utilizzo dei caratteri non sicuri
                                //nella stringa che le viene passata (playedMatches), che contiene il carattere"|".
                                onButtonClicked={playedMatches-> navController.navigate("activity2/${Uri.encode(playedMatches)}")}
                            )
                        }
                        composable("activity2/{playedMatches}"){backStackEntry->
                            //Passo il parametro da gestire in Activity2
                            Activity2(Uri.decode(backStackEntry.arguments?.getString("playedMatches").orEmpty()))
                        }
                    }
                }
            }
        }
    }
}
