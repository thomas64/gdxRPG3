package nl.t64.cot.components.event

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val EVENT_CONFIGS = "configs/events/"
private const val FILE_LIST = EVENT_CONFIGS + "_files.txt"

class EventContainer {

    private val events: Map<String, Event> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .map { Gdx.files.internal(EVENT_CONFIGS + it).readString() }
        .map { Utils.readValue<Event>(it) }
        .flatMap { it.toList() }
        .toMap()

    fun getEventById(eventId: String): Event = events[eventId]!!
    fun hasEventPlayed(eventId: String): Boolean = events[eventId]!!.hasPlayed

}
