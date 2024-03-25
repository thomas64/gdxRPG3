package nl.t64.cot.components.loot

import nl.t64.cot.ConfigDataLoader


class LootContainer {

    private val loot: Map<String, Loot> = ConfigDataLoader.createLoot()

    fun reset() {
        val originalLoot: Map<String, Loot> = ConfigDataLoader.createLoot()

        loot.filter { it.key.startsWith("chest") }
            .filter { it.value.isTaken() }
            .forEach { it.value.resetChest(originalLoot[it.key]!!) }

        loot.filter { it.key.startsWith("sparkle") }
            .filter { it.value.isTaken() }
            .filter { it.value.doesReset }
            .forEach { it.value.resetSparkle(originalLoot[it.key]!!) }

        loot.filter { it.key.startsWith("quest") }
            .forEach { it.value.resetQuest(originalLoot[it.key]!!)  }
    }

    fun getLoot(lootId: String): Loot {
        return loot[lootId] ?: Loot() // empty loot for a questId without a reward.
    }

}
