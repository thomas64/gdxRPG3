package nl.t64.cot.components.loot

import nl.t64.cot.resources.ConfigDataLoader


class LootContainer {

    private val loot: Map<String, Loot> = ConfigDataLoader.createLoot()

    fun reset() {
        val originalLoot: Map<String, Loot> = ConfigDataLoader.createLoot()

        loot.filter { it.key.startsWith("chest") }
            .filter { it.value.isTaken() }
            .forEach { it.value.resetChest(originalLoot[it.key]!!) }

        loot.filter { it.key.startsWith("sparkle") }
            .filter { it.value.isTaken() }
            .forEach { it.value.possibleResetSparkle(originalLoot[it.key]!!) }

        loot.filter { it.key.startsWith("quest") }
            .filterNot { it.key == "quest_mother_fairy" }
            .forEach { it.value.resetQuest(originalLoot[it.key]!!) }

        loot.filterNot { it.key.startsWith("chest") }
            .filterNot { it.key.startsWith("sparkle") }
            .filterNot { it.key.startsWith("quest") }
            .filter { it.key == "the_road_is_open" }
            .forEach { it.value.resetConversation(originalLoot[it.key]!!) }
    }

    fun getLoot(lootId: String): Loot {
        return loot[lootId] ?: Loot() // empty loot for a questId without a reward.
    }

}
