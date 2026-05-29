package com.unipd.dei2026.simon

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

//la classe SoundManager gestisce e riproduce i suoni brevi contenuti nei file audio contenuti nella risorsa raw
class SoundManager(context: Context) {

    //inizializzazione di SounPool
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    //inizializzazione della mappa dei suoni, in cui la chiave è il carattere corrispondente al bottone colorato
    // e il valore corrisponde alla sua risorsa audio corrispondente
    private val beepSounds: Map<Char, Int> = mapOf(
            'R' to soundPool.load(context, R.raw.red, 1),
            'M' to soundPool.load(context, R.raw.magenta, 1),
            'Y' to soundPool.load(context, R.raw.yellow, 1),
            'G' to soundPool.load(context, R.raw.green, 1),
            'C' to soundPool.load(context, R.raw.cyan, 1),
            'B' to soundPool.load(context, R.raw.blue, 1)
    )

    //la funzione playSound gestisce la riproduzione del suono
    fun playSound(char:Char){
        val selectedSound= beepSounds[char]
        if (selectedSound!=null && selectedSound!=0){
            soundPool.play(selectedSound, 1.0f,1.0f, 1,0, 1.0f  )
        }
    }

    //la funzione release() svuota la RAM dai suoni scaricati quando l'activity viene distrutta evitando il memory leak
    fun release(){
        soundPool.release()
    }


}

