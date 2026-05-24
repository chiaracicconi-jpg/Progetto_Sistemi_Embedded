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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

// La class Main Activity viene costruita prendendo come modello l'applicazione Secret Message
//Inserisco al suo interno le due schermate Activity1 e Activity2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SimonTheme {
                val navController = rememberNavController()
                val mainViewModel: SimonViewModel=viewModel()
                // Creo lo Scaffold che costituirà l'impalcatura del mio codice,
                // Vado a impostare il colore del background

                Scaffold(modifier = Modifier.fillMaxSize()

                                    // nel metodo  verticalGradient() creo la lista contenente i colori che andrò a sfumare(viola_chiaro, viola)
                                    .background(brush = Brush.verticalGradient(colors=listOf(Color(0xF32E1745), Color(0xFF7B50A4), Color(0xFFE6E6FA),Color(0xFF7B50A4), Color(0xF32E1745) ))),

                        //imposto lo sfondo trasparente, altrimenti otterrò uno sfondo bianco di default
                        containerColor = Color.Transparent) { innerPadding ->
                    NavHost(
                        navController = navController, startDestination = "matchHistory",
                        modifier = Modifier.padding(innerPadding)
                    ) {


                        composable("gameScreen") {
                            GameScreen(
                                simonViewModel = mainViewModel,
                                // definisco la funzione di callback per il composable GameScreen, onButtonClicked.
                                //onButtonClicked passa come parametro una stringa contente l'elenco delle partite giocate (gestito in MatchHistory)
                                // Importo la classe Uri dalla libreria Android (import android.net.Uri) che gestisce l'utilizzo dei caratteri non sicuri
                                //nella stringa che le viene passata (playedMatches), che contiene il carattere"|".
                                onButtonClicked={ navController.navigate("matchHistory")}
                            )
                        }
                        composable("matchHistory"){
                            MatchHistory(
                                simonViewModel = mainViewModel,
                                onPlayClicked={navController.navigate("gameScreen")},
                                onMatchClicked={score, data->
                                    val encoded=Uri.encode(data)
                                    navController.navigate("detail/$score/$encoded")
                                }
                            )
                        }
                        composable(
                            route="detail/{score}/{data}",
                            arguments=listOf(
                                navArgument("score") {type=NavType.IntType},
                                navArgument("data") {type=NavType.StringType})
                            ) {backStackEntry->
                            val score=backStackEntry.arguments?.getInt("score") ?: 0
                            val data=backStackEntry.arguments?.getString("data").orEmpty()
                            DetailScreen(score=score, data=data)
                        }

                    }
                }
            }
        }
    }
}
