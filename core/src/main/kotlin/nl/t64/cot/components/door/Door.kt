package nl.t64.cot.components.door

import nl.t64.cot.audio.AudioEvent


class Door(
    val type: DoorType = DoorType.SMALL,    // this will become replaced by the correct json value.
    val spriteId: String = "",
    val keyId: String? = null
) {

    val audio: AudioEvent = type.audioEvent
    val width: Float = type.width
    val height: Float = type.height
    var isLocked: Boolean = keyId != null
    var isClosed: Boolean = true

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
