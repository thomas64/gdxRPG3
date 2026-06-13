package nl.t64.cot.components.loot

import com.fasterxml.jackson.annotation.JsonProperty


private const val BONUS_PREFIX = "bonus_"

class Loot(
    var content: MutableMap<String, Int> = mutableMapOf(),
    @JsonProperty("condition")
    val conditions: List<String> = emptyList(),
    var trapLevel: Int = 0,
    var lockLevel: Int = 0,
    var xp: Int = 0,
    private val doesReset: Boolean = false,
    private var durability: MutableMap<String, Int> = mutableMapOf(),
) {

    companion object {
        fun createSingleItem(itemId: String): Loot {
            return Loot(mutableMapOf(itemId to 1))
        }

        fun createWithDurability(itemId: String, amount: Int, durability: Int): Loot {
            return Loot(mutableMapOf(itemId to amount),
                        durability = mutableMapOf(itemId to durability))
        }
    }

    fun getDurabilityFor(itemId: String): Int? {
        return durability[itemId]
    }

    fun resetChest(originalLoot: Loot) {
        trapLevel = originalLoot.trapLevel
        lockLevel = originalLoot.lockLevel
        content = when {
            doesReset -> originalLoot.content
            else -> mutableMapOf("gold" to 1)
        }
    }

    fun possibleResetSparkle(originalLoot: Loot) {
        if (doesReset) {
            content = originalLoot.content
        }
    }

    fun resetQuest(originalLoot: Loot) {
        content = originalLoot.content
        xp = originalLoot.xp
    }

    fun resetConversation(originalLoot: Loot) {
        content = originalLoot.content
        xp = originalLoot.xp
    }

    fun isEmpty(): Boolean {
        return content.isEmpty()
    }

    fun isTaken(): Boolean {
        return content.isEmpty()
    }

    fun clearContent() {
        content = mutableMapOf()
    }

    fun updateContent(newContent: MutableMap<String, Int>) {
        content = newContent
    }

    fun isTrapped(): Boolean {
        return trapLevel > 0
    }

    fun canDisarmTrap(mechanicLevel: Int): Boolean {
        return mechanicLevel >= trapLevel
    }

    fun disarmTrap() {
        trapLevel = 0
    }

    fun isLocked(): Boolean {
        return lockLevel > 0
    }

    fun canPickLock(thiefLevel: Int): Boolean {
        return thiefLevel >= lockLevel
    }

    fun pickLock() {
        lockLevel = 0
    }

    fun clearXp() {
        xp = 0
    }

    fun isXpGained(): Boolean {
        return xp == 0
    }

    fun handleBonus() {
        content
            .filterKeys { it.startsWith(BONUS_PREFIX) }
            .forEach { handleBonus(it.key, it.value) }
        removeBonus()
    }

    fun removeBonus() {
        content.keys.removeIf { it.startsWith(BONUS_PREFIX) }
    }

    private fun handleBonus(bonusItemId: String, bonusAmount: Int) {
        val itemId = bonusItemId.substring(BONUS_PREFIX.length)
        if (content.containsKey(itemId)) {
            val amount = content[itemId]!!
            content[itemId] = amount + bonusAmount
        } else {
            content[itemId] = bonusAmount
        }
    }

}
