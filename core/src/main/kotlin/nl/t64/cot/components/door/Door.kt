package nl.t64.cot.components.door

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.audio.AudioEvent


data class DoorProgress(
    val isUnlocked: Boolean = false,
    val isOpened: Boolean = false
) {

    // a door that nobody touched has no progress at all, so it does not have to be stored.
    fun isChanged(): Boolean {
        return this != DoorProgress()
    }
}

class Door(
    val type: DoorType = DoorType.SMALL,    // this will become replaced by the correct json value.
    val spriteId: String = "",
    val keyId: String? = null,
    @JsonProperty("condition")
    val conditions: List<String> = emptyList(),
    @JsonProperty("schedule")
    val scheduleConditions: List<String> = emptyList(),
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

    // only the deviation from the config is stored. the lock itself comes from the keyId in the config,
    // so removing or adding a keyId lands in an existing save file.
    fun toProgress(): DoorProgress {
        return DoorProgress(wasLockedOnce(), isOpen)
    }

    fun applyProgress(progress: DoorProgress) {
        if (progress.isUnlocked) {
            unlock()
        }
        if (progress.isOpened) {
            open()
        }
    }

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
