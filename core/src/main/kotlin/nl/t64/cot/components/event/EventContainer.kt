package nl.t64.cot.components.event

import nl.t64.cot.resources.ConfigDataLoader


class EventContainer {

    private val events: Map<String, Event> = ConfigDataLoader.createEvents()

    fun getEventById(eventId: String): Event {
        return events[eventId] ?: Event(type = "messagebox",
                                        text = listOf("This event is not handled correctly.",
                                                      "Your save file is of an older version than the game."))
    }

    fun getOnlyBattleGuideEvents(): List<String> {
        return events
            .filterKeys { it.startsWith("guide_event_battle_") }
            .filterKeys { hasEventPlayed(it) }
            .map { it.value.getReplacedText() }
            .reversed()
    }

    fun getAllNonBattlePlayedGuideEvents(): List<String> {
        return events
            .filterKeys { !it.startsWith("guide_event_battle_") }
            .filterKeys { it.startsWith("guide_event_") }
            .filterKeys { hasEventPlayed(it) }
            .map { it.value.getReplacedText() }
            .reversed()
    }

    fun hasEventPlayed(event: Event): Boolean {
        return event.hasPlayed
    }

    fun hasEventPlayed(eventId: String): Boolean {
        return events[eventId]!!.hasPlayed
    }

    fun toProgress(): Map<String, EventProgress> {
        return events
            .mapValues { (_, event) -> event.toProgress() }
            .filterValues { it.isChanged() }
    }

    fun applyProgress(progress: Map<String, EventProgress>) {
        progress.forEach { (id, p) -> events[id]!!.applyProgress(p) }
    }

}
