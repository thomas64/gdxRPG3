package nl.t64.cot.components.event

import nl.t64.cot.ConfigDataLoader


class EventContainer {

    private val events: Map<String, Event> = ConfigDataLoader.createEvents()

    fun getEventById(eventId: String): Event = events[eventId]
        ?: Event(type = "messagebox",
                 text = listOf("This event is not handled correctly.",
                               "Your save file is of an older version than the game."))

    fun getAllPlayedGuideEvents(): List<String> {
        return events
            .filterKeys { it.startsWith("guide_event_") }
            .filterKeys { hasEventPlayed(it) }
            .map { TextReplacer.replace(it.value.text) }
            .reversed()
    }

    fun hasEventPlayed(eventId: String): Boolean = events[eventId]!!.hasPlayed

}
