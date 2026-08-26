package nl.t64.cot.components.loot

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId


private const val BONUS_PREFIX = "bonus_"

data class LootProgress(
    val changedContent: Map<String, Int>? = null,
    val isTrapDisarmed: Boolean = false,
    val isLockPicked: Boolean = false,
    val isXpGained: Boolean = false
) {

    // loot that nobody touched has no progress at all, so it does not have to be stored.
    fun isChanged(): Boolean {
        return this != LootProgress()
    }
}

class Loot(
    var content: MutableMap<String, Int> = mutableMapOf(),
    @JsonProperty("condition")
    val conditions: List<String> = emptyList(),
    var trapLevel: Int = 0,
    var lockLevel: Int = 0,
    val gatherLevel: Int = 0,
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

    fun toProgress(lootFromConfig: Loot): LootProgress {
        return LootProgress(changedContent = getContentIfChanged(lootFromConfig),
                            isTrapDisarmed = hasDisarmedTrap(lootFromConfig),
                            isLockPicked = hasPickedLock(lootFromConfig),
                            isXpGained = hasGainedXp(lootFromConfig))
    }

    fun applyProgress(progress: LootProgress) {
        possibleChangeContent(progress.changedContent)

        if (progress.isTrapDisarmed) {
            disarmTrap()
        }
        if (progress.isLockPicked) {
            pickLock()
        }
        if (progress.isXpGained) {
            clearXp()
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

    fun canBeGatheredByParty(): Boolean {
        val bestRangerRankFromParty: Int = gameData.party.getBestSkillRank(SkillItemId.RANGER)
        return bestRangerRankFromParty >= gatherLevel
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

    // null means the content from save file was never touched, so the content from the config is kept.
    private fun getContentIfChanged(lootFromConfig: Loot): Map<String, Int>? {
        if (content == lootFromConfig.content) return null
        return content.toMutableMap()
    }

    private fun hasDisarmedTrap(lootFromConfig: Loot): Boolean {
        return trapLevel < lootFromConfig.trapLevel
    }

    private fun hasPickedLock(lootFromConfig: Loot): Boolean {
        return lockLevel < lootFromConfig.lockLevel
    }

    private fun hasGainedXp(lootFromConfig: Loot): Boolean {
        return xp < lootFromConfig.xp
    }

    private fun possibleChangeContent(changedContent: Map<String, Int>?) {
        if (changedContent == null) return
        content = changedContent.toMutableMap()
    }

}
