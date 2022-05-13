package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(

        Pair("3_starting_potions") { startingPotions },
        Pair("grace_ribbon") { graceRibbon },
        Pair("!cave_found") { caveNotYetFoundFirstCycle },
        Pair("first_equipment_item") { firstEquipmentItem },
        Pair("c_!quest_orc_guards_finished") { !questOrcGuardsFinished },

        Pair("i_!know_about_grace") { !knowAboutGrace },
        Pair("i_know_about_grace") { knowAboutGrace },
        Pair("i_!quest_mother_fairy_accepted") { !questMotherFairyAccepted },

        Pair("been_in_fairy_town") { beenInFairyTown },
        Pair("i_scroll_of_orc_obedience") { scrollOfOrcObedience },
        Pair("diplomat4") { diplomat4 },
        Pair("level10") { level10 },
        Pair("defeated_orc_guards") { defeatedOrcGuards },

        Pair("!has_talked_to_lennor") { hasNotYetTalkedToLennorFirstCycle },
        Pair("i_c_!quest_a_helping_horse_finished") { !questHelpingHorseCurrentFinished },
        Pair("i_r_quest_a_helping_horse_finished") { questHelpingHorseResetFinished },

        Pair("i_c_quest_pest_removal_unknown") { questPestRemovalUnknown },
        Pair("i_c_quest_pest_removal_known") { questPestRemovalKnown },
        Pair("i_c_quest_pest_removal_task_complete") { questPestRemovalTaskComplete },
        Pair("c_quest_pest_removal_known_or_lower") { questPestRemovalKnownOrLower },

        Pair("c_!quest_honeywood_soldiers_finished") { !questHoneywoodSoldiersFinished }
    )

    fun isMeetingConditions(conditionIds: List<String?>): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            else -> conditionIds.all { conditions[it]!!.invoke() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val startingPotions get() = hasEnoughOfItem("healing_potion", 3)
    private val graceRibbon get() = hasEnoughOfItem("grace_ribbon", 1)
    private val caveNotYetFoundFirstCycle
        get() = isQuestResetStateEqual("quest_grace_is_missing", QuestState.UNKNOWN)
                && isQuestTaskNumberComplete("quest_grace_is_missing", 1)
                && !isQuestTaskNumberComplete("quest_grace_is_missing", 2)
    private val firstEquipmentItem get() = hasEnoughOfOneOfTheseItems("basic_light_helmet")
    private val questOrcGuardsFinished get() = isQuestCurrentStateEqual("quest_orc_guards", QuestState.FINISHED)

    private val knowAboutGrace
        get() = isOneOfBothStatesEqualOrHigher("quest_orc_guards", QuestState.ACCEPTED)
                || isOneOfBothStatesEqualOrHigher("quest_mother_fairy", QuestState.ACCEPTED)
    private val questMotherFairyAccepted get() = isQuestCurrentStateEqualOrHigher("quest_mother_fairy", QuestState.ACCEPTED)

    private val beenInFairyTown get() = hasEventPlayed("find_great_tree")
    private val scrollOfOrcObedience get() = hasEnoughOfItem("scroll_of_orc_obedience", 1)
    private val diplomat4 get() = hasEnoughOfSkill(SkillItemId.DIPLOMAT, 4)
    private val level10 get() = hasMinimumLevelOf(10)
    private val defeatedOrcGuards get() = isBattleWon("quest_orc_guards")

    private val hasNotYetTalkedToLennorFirstCycle
        get() = isQuestResetStateEqual("quest_a_helping_horse", QuestState.UNKNOWN)
                && isCurrentPhraseId("quest_a_helping_horse", "1")
    private val questHelpingHorseCurrentFinished get() = isQuestCurrentStateEqual("quest_a_helping_horse", QuestState.FINISHED)
    private val questHelpingHorseResetFinished get() = isQuestResetStateEqual("quest_a_helping_horse", QuestState.FINISHED)

    private val questPestRemovalUnknown get() = isQuestCurrentStateEqual("quest_honeywood_inn_price-1", QuestState.UNKNOWN)
    private val questPestRemovalKnown get() = isQuestCurrentStateEqual("quest_honeywood_inn_price-1", QuestState.KNOWN)
    private val questPestRemovalTaskComplete
        get() = isQuestTaskNumberComplete("quest_honeywood_inn_price-1", 1)
                && !isQuestTaskNumberComplete("quest_honeywood_inn_price-1", 2)
    private val questPestRemovalKnownOrLower get() = isQuestCurrentStateEqualOrLower("quest_honeywood_inn_price-1", QuestState.KNOWN)

    private val questHoneywoodSoldiersFinished get() = isQuestCurrentStateEqual("quest_honeywood_soldiers", QuestState.FINISHED)

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

    private fun isQuestCurrentStateEqual(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).currentState == questState

    private fun isQuestResetStateEqual(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).resetState == questState

    private fun isQuestCurrentStateEqualOrHigher(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isCurrentStateEqualOrHigherThan(questState)

    private fun isOneOfBothStatesEqualOrHigher(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isOneOfBothStatesEqualOrHigherThan(questState)

    private fun isQuestCurrentStateEqualOrLower(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isCurrentStateEqualOrLowerThan(questState)

    private fun hasEventPlayed(eventId: String): Boolean =
        gameData.events.hasEventPlayed(eventId)

    private fun isBattleWon(battleId: String): Boolean =
        gameData.battles.isBattleWon(battleId)

    private fun isCurrentPhraseId(conversationId: String, currentPhraseId: String): Boolean =
        gameData.conversations.getConversationById(conversationId).currentPhraseId == currentPhraseId

}
