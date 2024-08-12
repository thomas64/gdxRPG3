package nl.t64.cot.components.door

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.audio.AudioEvent


class Door(
    val type: DoorType = DoorType.SMALL,    // this will become replaced by the correct json value.
    val spriteId: String = "",
    val keyId: String? = null,
    @JsonProperty(value = "condition")
    val conditionIds: List<String> = emptyList(),
    val openStartTime: String? = null,
    val openEndTime: String? = null,
    val message: String? = null
) {

    val audio: AudioEvent = type.audioEvent
    val width: Float = type.width
    val height: Float = type.height
    var isLocked: Boolean = if (keyId == null) false else true
    var isClosed: Boolean = true
    val isOpen: Boolean get() = !isClosed

    fun wasLockedOnce(): Boolean {
        return !isLocked && keyId != null
    }

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
