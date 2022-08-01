package nl.t64.cot.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant
import java.util.*
import kotlin.math.max


fun playSe(audioEvent: AudioEvent, isLooping: Boolean = false) {
    if (isLooping) {
        audioManager.handle(AudioCommand.SE_PLAY_LOOP, audioEvent)
    } else {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, audioEvent)
    }
}

fun stopSe(audioEvent: AudioEvent) {
    audioManager.handle(AudioCommand.SE_STOP, audioEvent)
}

fun stopAllSe() {
    audioManager.handle(AudioCommand.SE_STOP_ALL)
}

fun playBgm(audioEvent: AudioEvent, isLooping: Boolean = true) {
    if (isLooping) {
        audioManager.handle(AudioCommand.BGM_PLAY_LOOP, audioEvent)
    } else {
        audioManager.handle(AudioCommand.BGM_PLAY_ONCE, audioEvent)
    }
}

fun stopAllBgm() {
    audioManager.handle(AudioCommand.BGM_STOP_ALL)
}

fun playBgs(audioEvents: List<AudioEvent>) {
    audioEvents.forEach { playBgs(it) }
}

fun playBgs(audioEvent: AudioEvent) {
    audioManager.handle(AudioCommand.BGS_PLAY_LOOP, audioEvent)
}

fun stopBgs(audioEvent: AudioEvent) {
    audioManager.handle(AudioCommand.BGS_STOP, audioEvent)
}

class AudioManager {

    private val queuedBgm: EnumMap<AudioEvent, Music> = EnumMap(AudioEvent::class.java)
    private val queuedBgs: EnumMap<AudioEvent, Music> = EnumMap(AudioEvent::class.java)
    private val queuedMe: EnumMap<AudioEvent, Sound> = EnumMap(AudioEvent::class.java)
    private val queuedSe: EnumMap<AudioEvent, Sound> = EnumMap(AudioEvent::class.java)

    fun fadeBgmForClockWarning() {
        if (isThereAnyBgmPlaying() && !isBgmPlaying(AudioEvent.BGM_END_NEAR)) {
            certainBgmFade()
        } else if (!isThereAnyBgmPlaying()) {
            handle(AudioCommand.BGM_PLAY_LOOP, AudioEvent.BGM_END_NEAR)
        }
    }

    fun fadeAllInSeparateThreadForClockEnding(dt: Float) {
        Thread {
            while (audioManager.isBgmPlaying(AudioEvent.BGM_END_NEAR)) {
                Thread.sleep((dt * 1000f).toLong())
                audioManager.certainFadeBgmBgs()
            }
        }.start()
    }

    fun fadeBgmAndPlayBgm(toFade: AudioEvent, toPlay: AudioEvent) {
        if (isBgmPlaying(toFade)) {
            certainBgmFade()
        } else if (!isThereAnyBgmPlaying()) {
            handle(AudioCommand.BGM_PLAY_LOOP, toPlay)
        }
    }

    fun stopBgmAndPlayBgm(toStop: AudioEvent, toPlay: AudioEvent) {
        if (isBgmPlaying(toStop)) {
            handle(AudioCommand.BGM_STOP_ALL)
            handle(AudioCommand.BGM_PLAY_LOOP, toPlay)
        }
    }

    fun possibleBgmFade(bgm: AudioEvent) {
        if (isBgmPlaying(bgm)) {
            certainBgmFade()
        }
    }

    fun possibleBgmFade(currentBgm: AudioEvent, newBgm: AudioEvent) {
        if (currentBgm != newBgm) {
            certainBgmFade()
        }
    }

    fun possibleBgsFade(currentBgs: List<AudioEvent>, newBgs: List<AudioEvent>) {
        currentBgs
            .filter { it != AudioEvent.NONE }
            .filter { !newBgs.contains(it) }
            .forEach { fade(queuedBgs[it]!!, it.volume) }
    }

    fun certainFadeBgmBgs() {
        (queuedBgm + queuedBgs).forEach { fade(it.value, it.key.volume) }
    }

    fun possibleBgmSwitch(prevBgm: AudioEvent, nextBgm: AudioEvent) {
        if (prevBgm != nextBgm) {
            certainBgmSwitch(nextBgm)
        }
    }

    fun certainBgmSwitch(nextBgm: AudioEvent) {
        handle(AudioCommand.BGM_STOP_ALL)
        handle(AudioCommand.BGM_PLAY_LOOP, nextBgm)
    }

    fun possibleBgsSwitch(prevBgs: List<AudioEvent>, nextBgs: List<AudioEvent>) {
        prevBgs
            .filter { it != AudioEvent.NONE }
            .filter { !nextBgs.contains(it) }
            .forEach { handle(AudioCommand.BGS_STOP, it) }
        nextBgs
            .filter { it != AudioEvent.NONE }
            .filter { !prevBgs.contains(it) }
            .forEach { handle(AudioCommand.BGS_PLAY_LOOP, it) }
    }

    fun certainBgsSwitch(nextBgs: List<AudioEvent>) {
        handle(AudioCommand.BGS_STOP_ALL)
        nextBgs.forEach { handle(AudioCommand.BGS_PLAY_LOOP, it) }
    }

    fun handle(command: AudioCommand, event: AudioEvent) {
        when (command) {
            AudioCommand.BGM_PLAY_ONCE -> playBgm(event, false)
            AudioCommand.BGM_PLAY_LOOP -> playBgm(event, true)
            AudioCommand.BGM_STOP -> queuedBgm[event]?.stop()
            AudioCommand.BGM_PAUSE -> queuedBgm[event]!!.pause()

            AudioCommand.BGS_PLAY_ONCE -> playBgs(event, false)
            AudioCommand.BGS_PLAY_LOOP -> playBgs(event, true)
            AudioCommand.BGS_STOP -> queuedBgs[event]?.stop()
            AudioCommand.BGS_PAUSE -> queuedBgs[event]!!.pause()

            AudioCommand.ME_PLAY_ONCE -> playMe(event, false)
            AudioCommand.ME_PLAY_LOOP -> playMe(event, true)
            AudioCommand.ME_STOP -> queuedMe[event]!!.stop()

            AudioCommand.SE_PLAY_ONCE -> playSe(event, false)
            AudioCommand.SE_PLAY_LOOP -> playSe(event, true)
            AudioCommand.SE_STOP -> queuedSe[event]?.stop()
            else -> throw IllegalArgumentException("Call 'ALL' AudioCommands without second argument.")
        }
    }

    fun handle(command: AudioCommand) {
        when (command) {
            AudioCommand.BGM_STOP_ALL -> queuedBgm.values.forEach { it.stop() }
            AudioCommand.BGM_PAUSE_ALL -> queuedBgm.values.forEach { it.pause() }

            AudioCommand.BGS_STOP_ALL -> queuedBgs.values.forEach { it.stop() }
            AudioCommand.BGS_PAUSE_ALL -> queuedBgs.values.forEach { it.pause() }

            AudioCommand.SE_STOP_ALL -> queuedSe.values.forEach { it.stop() }
            else -> throw IllegalArgumentException("Call non-'ALL' AudioCommands with second argument.")
        }
    }

    fun dispose() {
        queuedBgm.values.forEach { it.dispose() }
        queuedBgs.values.forEach { it.dispose() }
        queuedMe.values.forEach { it.dispose() }
        queuedSe.values.forEach { it.dispose() }
    }

    private fun isBgmPlaying(audioEvent: AudioEvent): Boolean {
        return queuedBgm[audioEvent]?.isPlaying ?: false
    }

    private fun isThereAnyBgmPlaying(): Boolean {
        return queuedBgm.values.count { it.isPlaying } > 0
    }

    private fun certainBgmFade() {
        queuedBgm.forEach { fade(it.value, it.key.volume) }
    }

    private fun fade(bgmBgs: Music, defaultVolume: Float) {
        if (bgmBgs.isPlaying) {
            var volume = bgmBgs.volume
            if (volume > 0f) {
                volume -= defaultVolume / Constant.FADE_DURATION * Gdx.graphics.deltaTime
                bgmBgs.volume = max(volume, 0f)
            } else {
                bgmBgs.volume = defaultVolume
                bgmBgs.pause()
            }
        }
    }

    private fun playBgm(event: AudioEvent, isLooping: Boolean) {
        if (event.filePath.isNotEmpty()) {
            val bgm: Music
            if (queuedBgm.containsKey(event)) {
                bgm = queuedBgm[event]!!
            } else {
                bgm = resourceManager.getMusicAsset(event.filePath)
                queuedBgm[event] = bgm
            }
            if (preferenceManager.isMusicOn) {
                bgm.isLooping = isLooping
                bgm.play()
                bgm.volume = event.volume
            } else {
                bgm.stop()
            }
        }
    }

    private fun playBgs(event: AudioEvent, isLooping: Boolean) {
        if (event.filePath.isNotEmpty()) {
            val bgs: Music
            if (queuedBgs.containsKey(event)) {
                bgs = queuedBgs[event]!!
            } else {
                bgs = resourceManager.getMusicAsset(event.filePath)
                queuedBgs[event] = bgs
            }
            if (preferenceManager.isSoundOn) {
                if (!bgs.isPlaying) {
                    bgs.isLooping = isLooping
                    bgs.play()
                    bgs.volume = event.volume
                }
            } else {
                bgs.stop()
            }
        }
    }

    private fun playMe(event: AudioEvent, isLooping: Boolean) {
        val me: Sound
        if (queuedMe.containsKey(event)) {
            me = queuedMe[event]!!
        } else {
            me = resourceManager.getSoundAsset(event.filePath)
            queuedMe[event] = me
        }
        if (preferenceManager.isMusicOn) {
            val meId = me.play()
            me.setLooping(meId, isLooping)
        } else {
            me.stop()
        }
    }

    private fun playSe(event: AudioEvent, isLooping: Boolean) {
        val se: Sound
        if (queuedSe.containsKey(event)) {
            se = queuedSe[event]!!
        } else {
            se = resourceManager.getSoundAsset(event.filePath)
            queuedSe[event] = se
        }
        if (preferenceManager.isSoundOn) {
            val seId = se.play()
            se.setVolume(seId, event.volume)
            se.setLooping(seId, isLooping)
        } else {
            se.stop()
        }
    }

}
