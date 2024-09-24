package nl.t64.cot.audio

import nl.t64.cot.Utils.audioManager


fun playSe(audioEvent: AudioEvent, isLooping: Boolean = false) {
    if (audioEvent == AudioEvent.NONE) return
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

fun pauseAllBg() {
    audioManager.handle((AudioCommand.BGM_PAUSE_ALL))
    audioManager.handle((AudioCommand.BGS_PAUSE_ALL))
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
