package nl.t64.cot.components.loot


class SpoilsContainer {

    private val spoils: MutableMap<String, Spoil> = mutableMapOf()

    fun getByMapId(mapId: String): Map<String, Spoil> {
        return spoils.filter { it.value.mapId == mapId }
    }

    fun addSpoil(battleId: String, spoil: Spoil) {
        spoils[battleId] = spoil
    }

}
