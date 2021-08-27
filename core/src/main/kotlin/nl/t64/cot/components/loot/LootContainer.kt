package nl.t64.cot.components.loot

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val LOOT_CONFIGS = "configs/loot/"
private const val SPARKLE_FILE_LIST = LOOT_CONFIGS + "_files_sparkles.txt"
private const val CHEST_FILE_LIST = LOOT_CONFIGS + "_files_chests.txt"
private const val QUEST_FILE_LIST = LOOT_CONFIGS + "_files_quests.txt"
private const val CONVERSATION_FILE_LIST = LOOT_CONFIGS + "_files_conversations.txt"

class LootContainer {

    private val loot: Map<String, Loot> = loadLoot()

    fun getLoot(lootId: String): Loot {
        return if (loot.containsKey(lootId)) {
            loot[lootId]!!
        } else {
            Loot() // empty loot for a questId without a reward.
        }
    }

    private fun loadLoot(): Map<String, Loot> {
        val sparkles = getFileList(SPARKLE_FILE_LIST)
        val chests = getFileList(CHEST_FILE_LIST)
        val quests = getFileList(QUEST_FILE_LIST)
        val conversations = getFileList(CONVERSATION_FILE_LIST)
        return listOf(sparkles, chests, quests, conversations)
            .flatten()
            .map { Gdx.files.internal(LOOT_CONFIGS + it).readString() }
            .map { Utils.readValue<Loot>(it) }
            .flatMap { it.toList() }
            .toMap()
    }

    private fun getFileList(fileList: String): List<String> {
        return Gdx.files.internal(fileList).readString().split(System.lineSeparator())
    }

}
