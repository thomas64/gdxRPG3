package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.abilities.AbilityItemId
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.portal.Portal
import nl.t64.cot.constants.ScreenType


fun List<String>.areAllTrue(questId: String? = null): Boolean {
    return ConditionDatabase.isMeetingConditions(this, questId)
}

fun String.isTrue(): Boolean {
    return ConditionDatabase.isMeetingConditions(listOf(this))
}

object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(
        // @formatter:off
        "barbarian1"                    to { hasEnoughOfSkill(SkillItemId.BARBARIAN,    1) },
        "barbarian2"                    to { hasEnoughOfSkill(SkillItemId.BARBARIAN,    2) },
        "barbarian4"                    to { hasEnoughOfSkill(SkillItemId.BARBARIAN,    4) },
        "diplomat1"                     to { hasEnoughOfSkill(SkillItemId.DIPLOMAT,     1) },
        "diplomat2"                     to { hasEnoughOfSkill(SkillItemId.DIPLOMAT,     2) },
        "diplomat3"                     to { hasEnoughOfSkill(SkillItemId.DIPLOMAT,     3) },
        "jester1"                       to { hasEnoughOfSkill(SkillItemId.JESTER,       1) },
        "jester2"                       to { hasEnoughOfSkill(SkillItemId.JESTER,       2) },
        "warrior4"                      to { hasEnoughOfSkill(SkillItemId.WARRIOR,      4) },
        "wizard1"                       to { hasEnoughOfSkill(SkillItemId.WIZARD,       1) },
        "mozes_wizard1"                 to { hasMozesEnoughOfSkill(SkillItemId.WIZARD,  1) },
        "druid1"                        to { hasEnoughOfSkill(SkillItemId.DRUID,        1) },
        "druid2"                        to { hasEnoughOfSkill(SkillItemId.DRUID,        2) },
        "loremaster2"                   to { hasEnoughOfSkill(SkillItemId.LOREMASTER,   2) },

        "xp_>=_15"                      to { isXpGreaterThan(15) },
        "!been_in_fairy_town"           to { !hasEventPlayed("enter_great_tree") },
        "been_in_fairy_town"            to { hasEventPlayed("enter_great_tree") },
        "defeated_orc_guards"           to { isBattleWon("quest_orc_guards") },
        "shield_spell"                  to { hasSpell("magic_shield", "mozes") },
        "!black_asked_four"             to { !blackAskedFour },
        "black_asked_four"              to { blackAskedFour },
        "alone_in_party"                to { isAloneInParty },
        "!alone_in_party"               to { !isAloneInParty },
        "fairy_portal_active"           to { areBothPortalsActive },
        "!fairy_portal_active"          to { isPortalFairyInactiveAndPortalHoneywoodActive },
        "is_specific_time"              to { isBlackCurrentlyNotOpeningHisDoor },
        "is_lastdenn_guard_alive"       to { !isBattleWon("guarding_till_1500") },
        "!is_lastdenn_guard_alive"      to { isBattleWon("guarding_till_1500") },
        "is_lastdenn_entrance_closed"   to { isLastdennEntranceClosed },
        "is_garrin_possessed"           to { isGarrinPossessed },
        "!is_garrin_possessed"          to { !isGarrinPossessed },
        "witnessed_garrin_possession"   to { gameData.cutscenes.isPlayedThisCycle(ScreenType.SCENE_GHOST_POSSESSES_GARRIN) },
        "is_reignald_recruited"         to { hasReignaldBeenRecruited },
        "!is_reignald_recruited"        to { !hasReignaldBeenRecruited },
        "is_lastdenn_cell_locked"       to { gameData.doors.getDoor("door_lastdenn_jail_b1").isLocked },
        "should_guard_be_giving_key"    to { shouldGuardBeGivingKey },
        // @formatter:on
    )

    fun isMeetingConditions(conditionIds: List<String?>, questId: String? = null): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            else -> conditionIds.all { isMeetingCondition(it!!, questId) }
        }
    }

    private fun isMeetingCondition(conditionId: String, questId: String?): Boolean {
        return if (conditionId.contains("_x_")) {
            ConditionConverter.isQuestFailed(conditionId, questId)
        } else if (conditionId.contains("_q_")) {
            ConditionConverter.isMeetingQuestCondition(conditionId, questId)
        } else if (conditionId.contains("_cycle_")) {
            ConditionConverter.isMeetingCycleCondition(conditionId)
        } else if (conditionId.contains("_item_inv_") && conditionId.contains("_n_")) {
            ConditionConverter.isMeetingItemInventoryCondition(conditionId)
        } else if (conditionId.contains("_item_eqp_") && conditionId.contains("_n_")) {
            ConditionConverter.isMeetingItemEquipmentCondition(conditionId)
        } else if (conditionId.contains("_item_") && conditionId.contains("_n_")) {
            ConditionConverter.isMeetingItemCondition(conditionId)
        } else if (conditionId.contains("_time_")) {
            ConditionConverter.isMeetingTimeCondition(conditionId)
        } else if (conditionId.contains("_conv_")) {
            ConditionConverter.isMeetingConversationCondition(conditionId)
        } else if (conditionId.contains("_loot_")) {
            ConditionConverter.isMeetingLootCondition(conditionId)
        } else if (conditionId.contains("_hero_")) {
            ConditionConverter.isMeetingHeroCondition(conditionId)
        } else {
            if (conditionId.startsWith("ii_")) {
                conditions[conditionId.removePrefix("ii_")]!!.invoke()
            } else if (conditionId.startsWith("i_")) {
                conditions[conditionId.removePrefix("i_")]!!.invoke()
            } else {
                conditions[conditionId]!!.invoke()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val blackAskedFour
        get() = isTargetAlternateUsed("quest_get_tow_rope", "13") // "_13_"
            || isMeetingCondition("_conv_quest_get_horseshoes_==_200", null)
    private val isAloneInParty
        get() = hasAmountOfPartyMembers(1)
            && !gameData.heroes.hasAnyoneBeenRecruited()
    private val areBothPortalsActive
        get() = gameData.portals.isActivated(Portal.HONEYWOOD_GREAT_TREE.name)
            && gameData.portals.isActivated(Portal.HONEYWOOD_HOUSE_ELDER_B2.name)
    private val isPortalFairyInactiveAndPortalHoneywoodActive
        get() = !gameData.portals.isActivated(Portal.HONEYWOOD_GREAT_TREE.name)
            && gameData.portals.isActivated(Portal.HONEYWOOD_HOUSE_ELDER_B2.name)
    private val isBlackCurrentlyNotOpeningHisDoor
        get() = gameData.clock.isCurrentTimeBefore("07:35")
            || gameData.clock.isCurrentTimeAfter("07:38")
    private val isLastdennEntranceClosed
        get() = gameData.clock.isCurrentTimeBefore("15:00")
            && isMeetingCondition("_conv_guarding_till_1500_<_100", null)
            && !isBattleWon("guarding_till_1500")
    private val shouldGuardBeGivingKey
        get() = !gameData.inventory.contains("key_lastdenn_jail")
            && ("_c_==_a_q_quest_lastdenn_ryiah".isTrue()
                || ("_r_==_a_q_quest_lastdenn_ryiah".isTrue() && "_c_!=_f_q_quest_lastdenn_ryiah".isTrue()))
    private val hasReignaldBeenRecruited
        get() = !gameData.heroes.contains("reignald")
            || gameData.party.contains("reignald")
            || gameData.heroes.getCertainHero("reignald").hasBeenRecruited
    private val isGarrinPossessed
        get() = gameData.quests.getQuestById("quest_lastdenn_garrin").tasks["2"]!!.isComplete.not() // "_2_"

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun isXpGreaterThan(requestedXp: Int): Boolean =
        gameData.party.getCertainHero("mozes").totalXp >= requestedXp

    private fun hasMozesEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.getCertainHero("mozes").getCalculatedTotalSkillOf(skillItemId) >= rank

    private fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.hasEnoughOfSkill(skillItemId, rank)

    private fun hasSpell(spellId: String, heroId: String): Boolean =
        gameData.party.getCertainHero(heroId).getAllAbilities()
            .any { it.id == AbilityItemId.valueOf(spellId.uppercase()) }

    private fun isTargetAlternateUsed(questId: String, questTaskId: String): Boolean =
        gameData.quests.getQuestById(questId).tasks[questTaskId]!!.isTargetAlternateUsed

    private fun hasEventPlayed(eventId: String): Boolean =
        gameData.events.hasEventPlayed(eventId)

    private fun isBattleWon(battleId: String): Boolean =
        gameData.battles.isBattleWon(battleId)

    private fun hasAmountOfPartyMembers(amount: Int): Boolean =
        gameData.party.size == amount

}
