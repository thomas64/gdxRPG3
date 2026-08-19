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
            // the rewards for mother fairy should not be reset. they should only be given once and aren't even quest related.
            // they are given when they first meet mother fairy. not when they complete the quest. without this filter,
            // that could happen, in another cycle, when completing the quest. that's not what we want.
            .filterNot { it.key == "quest_mother_fairy" }
            .forEach { it.value.resetQuest(originalLoot[it.key]!!) }

        loot.filterNot { it.key.startsWith("chest") }
            .filterNot { it.key.startsWith("sparkle") }
            .filterNot { it.key.startsWith("quest") }
            .forEach { it.value.resetConversation(originalLoot[it.key]!!) }
    }

    fun getLoot(lootId: String): Loot {
        return loot[lootId] ?: Loot() // empty loot for a questId without a reward.
    }

}
