package nl.t64.cot.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant
import kotlin.math.max


private const val BGM_VOLUME = 0.1f
private const val BGS_VOLUME = 0.2f

class AudioManager {

    private val queuedBgm: MutableMap<String, Music> = HashMap()
    private val queuedBgs: MutableMap<String, Music> = HashMap()
    private val queuedMe: MutableMap<String, Sound> = HashMap()
    private val queuedSe: MutableMap<String, Sound> = HashMap()

    fun possibleBgmFade(currentBgm: AudioEvent, newBgm: AudioEvent) {
        if (currentBgm != newBgm) {
            queuedBgm.values.forEach { fade(it, BGM_VOLUME) }
        }
    }

    fun possibleBgsFade(currentBgs: List<AudioEvent>, newBgs: List<AudioEvent>) {
        currentBgs
            .filter { it != AudioEvent.NONE }
            .filter { !newBgs.contains(it) }
            .forEach { fade(queuedBgs[it.filePath]!!, BGS_VOLUME) }
    }

    fun possibleBgmSwitch(prevBgm: AudioEvent, nextBgm: AudioEvent) {
        if (prevBgm != nextBgm) {
            bgmSwitch(nextBgm)
        }
    }

    fun bgmSwitch(nextBgm: AudioEvent) {
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

    fun bgsSwitch(nextBgs: List<AudioEvent>) {
        handle(AudioCommand.BGS_STOP_ALL)
        nextBgs.forEach { handle(AudioCommand.BGS_PLAY_LOOP, it) }
    }

    fun fadeBgmBgs() {
        queuedBgm.values.forEach { fade(it, BGM_VOLUME) }
        queuedBgs.values.forEach { fade(it, BGS_VOLUME) }
    }

    fun handle(command: AudioCommand, events: List<AudioEvent>) {
        events.forEach { handle(command, it) }
    }

    fun handle(command: AudioCommand, event: AudioEvent) {
        when (command) {
            AudioCommand.BGM_PLAY_ONCE -> playBgm(event.filePath, false)
            AudioCommand.BGM_PLAY_LOOP -> playBgm(event.filePath, true)
            AudioCommand.BGM_STOP -> queuedBgm[event.filePath]?.stop()
            AudioCommand.BGM_PAUSE -> queuedBgm[event.filePath]!!.pause()

            AudioCommand.BGS_PLAY_ONCE -> playBgs(event.filePath, false)
            AudioCommand.BGS_PLAY_LOOP -> playBgs(event.filePath, true)
            AudioCommand.BGS_STOP -> queuedBgs[event.filePath]?.stop()
            AudioCommand.BGS_PAUSE -> queuedBgs[event.filePath]!!.pause()

            AudioCommand.ME_PLAY_ONCE -> playMe(event.filePath, false)
            AudioCommand.ME_PLAY_LOOP -> playMe(event.filePath, true)
            AudioCommand.ME_STOP -> queuedMe[event.filePath]!!.stop()

            AudioCommand.SE_PLAY_ONCE -> playSe(event, false)
            AudioCommand.SE_PLAY_LOOP -> playSe(event, true)
            AudioCommand.SE_STOP -> queuedSe[event.filePath]?.stop()
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

    private fun playBgm(filePath: String, isLooping: Boolean) {
        if (filePath.isEmpty()) {
            return
        }
        val bgm: Music
        if (queuedBgm.containsKey(filePath)) {
            bgm = queuedBgm[filePath]!!
        } else {
            bgm = resourceManager.getMusicAsset(filePath)
            queuedBgm[filePath] = bgm
        }
        if (preferenceManager.isMusicOn) {
            bgm.isLooping = isLooping
            bgm.play()
            bgm.volume = BGM_VOLUME
        } else {
            bgm.stop()
        }
    }

    private fun playBgs(filePath: String, isLooping: Boolean) {
        if (filePath.isEmpty()) {
            return
        }
        val bgs: Music
        if (queuedBgs.containsKey(filePath)) {
            bgs = queuedBgs[filePath]!!
        } else {
            bgs = resourceManager.getMusicAsset(filePath)
            queuedBgs[filePath] = bgs
        }
        if (preferenceManager.isSoundOn) {
            bgs.isLooping = isLooping
            bgs.play()
            bgs.volume = BGS_VOLUME
        } else {
            bgs.stop()
        }
    }

    private fun playMe(filePath: String, isLooping: Boolean) {
        val me: Sound
        if (queuedMe.containsKey(filePath)) {
            me = queuedMe[filePath]!!
        } else {
            me = resourceManager.getSoundAsset(filePath)
            queuedMe[filePath] = me
        }
        if (preferenceManager.isMusicOn) {
            val meId = me.play()
            me.setLooping(meId, isLooping)
        } else {
            me.stop()
        }
    }

    private fun playSe(event: AudioEvent, isLooping: Boolean) {
        val filePath = event.filePath
        val volume = event.volume
        val se: Sound
        if (queuedSe.containsKey(filePath)) {
            se = queuedSe[filePath]!!
        } else {
            se = resourceManager.getSoundAsset(filePath)
            queuedSe[filePath] = se
        }
        if (preferenceManager.isSoundOn) {
            val seId = se.play()
            se.setVolume(seId, volume)
            se.setLooping(seId, isLooping)
        } else {
            se.stop()
        }
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

}
