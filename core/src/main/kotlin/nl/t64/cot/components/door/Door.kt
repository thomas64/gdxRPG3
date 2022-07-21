package nl.t64.cot.components.door

import nl.t64.cot.audio.AudioEvent


class Door(
    val type: DoorType = DoorType.SMALL,    // this will become replaced by the correct json value.
    val spriteId: String = "",
    val keyId: String? = null,
    val openStartTime: String? = null,
    val openEndTime: String? = null
) {

    val audio: AudioEvent = type.audioEvent
    val width: Float = type.width
    val height: Float = type.height
    var isLocked: Boolean = keyId != null
    var isClosed: Boolean = true
    val isOpen: Boolean get() = !isClosed

    fun unlock() {
        isLocked = false
    }

    fun lock() {
        isLocked = true
    }

    fun open() {
        isClosed = false
    }

    fun close() {
        isClosed = true
    }

}
