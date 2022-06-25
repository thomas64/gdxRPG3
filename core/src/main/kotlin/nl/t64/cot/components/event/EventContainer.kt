package nl.t64.cot.components.event

import nl.t64.cot.ConfigDataLoader


class EventContainer {

    private val events: Map<String, Event> = ConfigDataLoader.createEvents()

    fun getEventById(eventId: String): Event = events[eventId] ?: Event("Dummy, will throw exception.")
    fun hasEventPlayed(eventId: String): Boolean = events[eventId]!!.hasPlayed

}
