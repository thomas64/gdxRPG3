package nl.t64.cot

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import nl.t64.cot.components.battle.Battle
import nl.t64.cot.components.battle.EnemyItem
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.door.Door
import nl.t64.cot.components.event.Event
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.components.quest.QuestGraph


object ConfigDataLoader {

    fun createBattles(): Map<String, Battle> {
        return loadConfigData<Battle>("battles")
    }

    fun createDoors(): Map<String, Door> {
        return loadConfigData<Door>("doors")
    }

    fun createEvents(): Map<String, Event> {
        return loadConfigData<Event>("events")
    }

    fun createLoot(): Map<String, Loot> {
        return loadConfigData<Loot>("loot")
    }

    fun createNotes(): Map<String, ConversationGraph> {
        return loadConfigData<ConversationGraph>("notes")
    }

    fun createConversations(): Map<String, ConversationGraph> {
        return loadConfigData<ConversationGraph>("conversations")
            .mapValues { it.value.copy(id = it.key) }
            .onEach { it.value.initId() }
    }

    fun createItems(): Map<String, InventoryItem> {
        return loadConfigData<InventoryItem>("inventory")
            .mapValues { it.value.copy(id = it.key) }
    }

    fun createQuests(): Map<String, QuestGraph> {
        return loadConfigData<QuestGraph>("quests")
            .mapValues { it.value.copy(id = it.key) }
    }

    fun createSpells(): Map<String, SpellItem> {
        return loadConfigData<SpellItem>("spells")
            .mapValues { it.value.copy(id = it.key) }
    }

    fun createSkills(): Map<String, SkillItem> {
        return loadConfigData<SkillItem>("skills")
            .mapValues {
                it.value.copy(id = SkillItemId.valueOf(it.key.uppercase()),
                              name = it.key.replaceFirstChar(Char::uppercase))
            }
    }

    fun createHeroes(): MutableMap<String, HeroItem> {
        val json = readString("characters", "hero1.json")
        return readValue<HeroItem>(json)
            .mapValues { it.value.copy(id = it.key) }
            .toMutableMap()
    }

    fun createEnemies(): Map<String, EnemyItem> {
        val json = readString("characters", "enemy1.json")
        return readValue<EnemyItem>(json)
            .mapValues { it.value.copy(id = it.key) }
    }

    fun createStats(): Map<String, StatItem> {
        val json = readString("stats", "stats.json")
        return readValue<StatItem>(json)
            .mapValues {
                it.value.copy(id = StatItemId.valueOf(it.key.uppercase()),
                              name = it.key.replaceFirstChar(Char::uppercase))
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private inline fun <reified T> loadConfigData(path: String): Map<String, T> {
        return getListWithFilenames(path)
            .map { readString(path, it) }
            .map { readValue<T>(it) }
            .flatMap { it.toList() }
            .toMap()
    }

    private fun getListWithFilenames(path: String): List<String> {
        return readString(path, "_files.txt")
            .split(System.lineSeparator())
            .filter { it.isNotBlank() }
    }

    private fun readString(path: String, filename: String): String {
        return Gdx.files.internal("configs/${path}/${filename}").readString()
    }

    private inline fun <reified T> readValue(json: String): Map<String, T> {
        val mapper = jacksonObjectMapper()
        val valueType = mapper.typeFactory.constructMapType(Map::class.java, String::class.java, T::class.java)
        return mapper.readValue(json, valueType)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun <T> readValue(json: String, clazz: Class<T>): HashMap<String, T> {
        val mapper = jacksonObjectMapper()
        val valueType = mapper.typeFactory.constructMapType(HashMap::class.java, String::class.java, clazz)
        return mapper.readValue(json, valueType)
    }

    private fun <T> readListValue(json: String, clazz: Class<T>): Map<String, List<T>> {
        val mapper = jacksonObjectMapper()
        val valueType = object : TypeReference<Map<String, List<T>>>() {}
        return mapper.readValue(json, valueType)
    }

}
