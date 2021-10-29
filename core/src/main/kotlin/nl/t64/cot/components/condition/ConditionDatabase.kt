package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(
        Pair("quest_intro_not_finished") { questIntroNotFinished },
        Pair("3_starting_potions") { startingPotions },
        Pair("grace_ribbon") { graceRibbon },
        Pair("cave_not_yet_found") { caveNotYetFound },
        Pair("first_equipment_item") { firstEquipmentItem },
        Pair("quest_orc_guards_not_finished") { questOrcGuardsNotFinished },
        Pair("i_!quest_orc_guards_accepted") { !questOrcGuardsAccepted },
        Pair("i_quest_orc_guards_accepted") { questOrcGuardsAccepted },
        Pair("i_!quest_mother_fairy_accepted") { !questMotherFairyAccepted },
        Pair("i_quest_mother_fairy_accepted") { questMotherFairyAccepted },
        Pair("been_in_fairy_town") { beenInFairyTown },
        Pair("i_!orc_amulet") { !orcAmulet },
        Pair("i_orc_amulet") { orcAmulet },
        Pair("i_mask_of_ardor") { maskOfArdor },
        Pair("i_diplomat2") { diplomat2 },
        Pair("i_level10") { level10 },
        Pair("defeated_orc_guards") { defeatedOrcGuards },

        Pair("diplomat3") { diplomat3 },
        Pair("key_mysterious_tunnel") { keyMysteriousTunnel },
        Pair("quest4_known") { quest4Known },
        Pair("i_quest6_task3") { quest6Task3 },
        Pair("quest6_known") { quest6Known },
        Pair("i_quest6_unclaimed") { quest6Unclaimed },
        Pair("i_quest7_task3") { quest7Task3 },
        Pair("quest7_unknown") { quest7Unknown },
        Pair("i_quest7_unclaimed") { quest7Unclaimed }
    )

    fun isMeetingConditions(conditionIds: List<String?>): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            else -> conditionIds.all { conditions[it]!!.invoke() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val questIntroNotFinished get() = !isQuestTaskNumberComplete("quest_intro", 1)
    private val startingPotions get() = hasEnoughOfItem("healing_potion", 3)
    private val graceRibbon get() = hasEnoughOfItem("grace_ribbon", 1)
    private val caveNotYetFound
        get() = isQuestTaskNumberComplete("quest_grace_is_missing", 1)
                && !isQuestTaskNumberComplete("quest_grace_is_missing", 2)
    private val firstEquipmentItem get() = hasEnoughOfOneOfTheseItems("basic_light_helmet")
    private val questOrcGuardsNotFinished get() = isQuestStateEqualOrLower("quest_orc_guards", QuestState.ACCEPTED)
    private val questOrcGuardsAccepted get() = isQuestStateEqualOrHigher("quest_orc_guards", QuestState.ACCEPTED)
    private val questMotherFairyAccepted get() = isQuestStateEqualOrHigher("quest_mother_fairy", QuestState.ACCEPTED)
    private val beenInFairyTown get() = hasEventPlayed("find_great_tree")
    private val orcAmulet get() = hasItemEquipped("transformation_orc")
    private val diplomat2 get() = hasEnoughOfSkill(SkillItemId.DIPLOMAT, 2)
    private val maskOfArdor get() = hasEnoughOfItem("mask_of_ardor", 1)
    private val level10 get() = hasMinimumLevelOf(10)
    private val defeatedOrcGuards get() = isBattleWon("quest_orc_guards")

    private val diplomat3 get() = hasEnoughOfSkill(SkillItemId.DIPLOMAT, 3)
    private val keyMysteriousTunnel get() = hasEnoughOfItem("key_mysterious_tunnel", 1)
    private val quest4Known get() = isQuestStateEqualOrHigher("quest0004", QuestState.KNOWN)
    private val quest6Task3 get() = isQuestTaskNumberComplete("quest0006", 3)
    private val quest6Known get() = isQuestStateEqualOrHigher("quest0006", QuestState.KNOWN)
    private val quest6Unclaimed get() = isQuestStateEqualOrHigher("quest0006", QuestState.UNCLAIMED)
    private val quest7Task3 get() = isQuestTaskNumberComplete("quest0007", 3)
    private val quest7Unknown get() = isQuestStateEqualOrLower("quest0007", QuestState.UNKNOWN)
    private val quest7Unclaimed get() = isQuestStateEqualOrHigher("quest0007", QuestState.UNCLAIMED)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.hasEnoughOfSkill(skillItemId, rank)

    private fun hasMinimumLevelOf(requestedLevel: Int): Boolean =
        gameData.party.getHero(0).getLevel() >= requestedLevel

    private fun hasEnoughOfOneOfTheseItems(vararg inventoryItemIds: String): Boolean =
        inventoryItemIds.any { hasEnoughOfItem(it, 1) }

    private fun hasEnoughOfItem(inventoryItemId: String, amount: Int): Boolean =
        gameData.inventory.hasEnoughOfItem(inventoryItemId, amount)
                || gameData.party.hasItemInEquipment(inventoryItemId, amount)

    private fun hasItemEquipped(inventoryItemId: String): Boolean =
        gameData.party.getPlayer().hasInventoryItem(inventoryItemId)

    private fun isQuestTaskNumberComplete(questId: String, taskNumber: Int): Boolean =
        gameData.quests.getQuestById(questId).isTaskComplete(taskNumber.toString())

    private fun isQuestStateEqualOrHigher(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isCurrentStateEqualOrHigherThan(questState)

    private fun isQuestStateEqualOrLower(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isCurrentStateEqualOrLowerThan(questState)

    private fun hasEventPlayed(eventId: String): Boolean =
        gameData.events.hasEventPlayed(eventId)

    private fun isBattleWon(battleId: String): Boolean =
        gameData.battles.isBattleWon(battleId)

}
