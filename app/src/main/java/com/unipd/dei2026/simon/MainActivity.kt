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

// La class Main Activity viene costruita prendendo come modello l'applicazione Secret Message e ReciclerSimple
//Inserisco al suo interno le schermate GameScreen, MatchHistory, DetailActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SimonTheme {
                val navController = rememberNavController()
                // passo il ViewModel definito in una schermata apposita nella MainActivity e poi all'interno delle tre schermate
                val mainViewModel: SimonViewModel=viewModel()

                // Creo lo Scaffold che costituirà l'impalcatura del mio codice,
                // Vado a impostare il colore del background

                Scaffold(modifier = Modifier.fillMaxSize()

                                    // nel metodo  verticalGradient() creo la lista contenente i colori che andrò a sfumare(lilla, viola_chiaro, viola_scuro)
                                    .background(brush = Brush.verticalGradient(colors=listOf(Color(0xF32E1745), Color(0xFF7B50A4), Color(0xFFD0BCFF),Color(0xFF7B50A4), Color(0xF32E1745) ))),

                        //imposto lo sfondo trasparente, altrimenti otterrò uno sfondo bianco di default
                        containerColor = Color.Transparent) { innerPadding ->
                    NavHost(
                        //imposto come schermata iniziale MatchHistory, pertanto la collego come destinazione iniziale
                        navController = navController, startDestination = "matchHistory",
                        modifier = Modifier.padding(innerPadding)
                    ) {


                        composable("gameScreen") {
                            GameScreen(
                                simonViewModel = mainViewModel,
                                // definisco la funzione di callback per il composable GameScreen, onButtonClicked.
                                //onButtonClicked mi garantisce il passaggio alla MatchHistory (tramite il pusante "End" o "Back" di sistema)
                                onButtonClicked={ navController.navigate("matchHistory")}
                            )
                        }
                        composable("matchHistory"){
                            MatchHistory(
                                simonViewModel = mainViewModel,
                                // definisco la funzione di callback per MatchHistory, onPlayClicked.
                                //onPlayClicked mi garantisce il passaggio alla GameScreen tramite il floating action button
                                onPlayClicked={navController.navigate("gameScreen")},
                                // definisco la funzione di callback per MatchHistory, onMatchClicked.
                                //onMatchClicked mi garantisce il passaggio alla DetailScreen dei dati della partita (punteggio, sequenza)
                                //utilizzo il metodo Uri.encode() per passare in modo sicuro la sequenza
                                //la navigazione mi gestisce la rotta alla nuova schermata
                                onMatchClicked={score, data->
                                    val encoded=Uri.encode(data)
                                    navController.navigate("detail/$score/$encoded")
                                }
                            )
                        }
                        composable(
                            //la DetailScreen viene gestita come l'omonima schermata in RecyclerSimple
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
