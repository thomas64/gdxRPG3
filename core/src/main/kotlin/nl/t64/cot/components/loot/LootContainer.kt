package nl.t64.cot.components.loot

import nl.t64.cot.ConfigDataLoader


class LootContainer {

    private val loot: Map<String, Loot> = ConfigDataLoader.createLoot()

    fun getLoot(lootId: String): Loot {
        return if (loot.containsKey(lootId)) {
            loot[lootId]!!
        } else {
            Loot() // empty loot for a questId without a reward.
        }
    }

}
