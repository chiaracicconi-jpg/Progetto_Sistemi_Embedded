package com.unipd.dei2026.simon

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val beepSounds: Map<Char, Int> = mapOf(
            'R' to soundPool.load(context, R.raw.red, 1),
            'M' to soundPool.load(context, R.raw.magenta, 1),
            'Y' to soundPool.load(context, R.raw.yellow, 1),
            'G' to soundPool.load(context, R.raw.green, 1),
            'C' to soundPool.load(context, R.raw.cyan, 1),
            'B' to soundPool.load(context, R.raw.blue, 1)
    )

    fun playSound(char:Char){
        val selectedSound= beepSounds[char]
        if (selectedSound!=null && selectedSound!=0){
            soundPool.play(selectedSound, 1.0f,1.0f, 1,0, 1.0f  )
        }
    }

    fun release(){
        soundPool.release()
    }


}

