package nl.t64.cot.components.loot


class SpoilsContainer {

    private val spoils: MutableMap<String, Spoil> = mutableMapOf()

    fun getByMapId(mapId: String): Map<String, Spoil> {
        return spoils.filterValues { it.mapId == mapId }
    }

    fun containsActiveSpoil(conversationId: String?): Boolean {
        return spoils[conversationId]?.loot?.isTaken() == false
    }

    fun getByConversationId(conversationId: String): Spoil {
        return spoils[conversationId]!!
    }

    fun addSpoil(battleOrConversationId: String, spoil: Spoil) {
        spoils[battleOrConversationId] = spoil
    }

}
