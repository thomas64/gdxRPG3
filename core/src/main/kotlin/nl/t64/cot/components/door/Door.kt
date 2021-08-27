package nl.t64.cot.components.door

import nl.t64.cot.audio.AudioEvent


class Door(
    val type: DoorType = DoorType.SMALL,    // this will become replaced by the correct json value.
    val spriteId: String = "",
    val keyId: String? = null
) {

    lateinit var audio: AudioEvent
    var width: Float = 0f
    var height: Float = 0f
    var isLocked = false
    var isClosed = false

    fun unlock() {
        isLocked = false
    }

    fun open() {
        isClosed = false
    }

    fun close() {
        isClosed = true
    }

}
