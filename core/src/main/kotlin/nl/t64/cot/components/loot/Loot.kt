package nl.t64.cot.components.loot


private const val BONUS_PREFIX = "bonus_"

class Loot(
    var content: MutableMap<String, Int> = mutableMapOf(),
    var trapLevel: Int = 0,
    var lockLevel: Int = 0,
    var xp: Int = 0
) {

    fun isTaken(): Boolean =
        content.isEmpty()

    fun clearContent() {
        content = mutableMapOf()
    }

    fun updateContent(newContent: MutableMap<String, Int>) {
        content = newContent
    }

    fun isTrapped(): Boolean =
        trapLevel > 0

    fun canDisarmTrap(mechanicLevel: Int): Boolean {
        return mechanicLevel >= trapLevel
    }

    fun disarmTrap() {
        trapLevel = 0
    }

    fun isLocked(): Boolean =
        lockLevel > 0

    fun canPickLock(thiefLevel: Int): Boolean {
        return thiefLevel >= lockLevel
    }

    fun pickLock() {
        lockLevel = 0
    }

    fun clearXp() {
        xp = 0
    }

    fun isXpGained(): Boolean =
        xp == 0

    fun handleBonus() {
        content.entries
            .filter { it.key.startsWith(BONUS_PREFIX) }
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
