package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.abilities.BattleAbilityItem


class AttackAction(
    private val currentParticipant: Participant,
    private val currentTarget: Participant,
    private val selectedAttack: BattleAbilityItem,
) {
    private val attacker: Character = currentParticipant.character
    private val target: Character = currentTarget.character

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant, battleId: String): AttackAction {
            val allAbilities: List<BattleAbilityItem> = currentEnemy.getBattleAbilities()
            val ability: BattleAbilityItem = (
                allAbilities
                    .filter { it.ap <= currentEnemy.currentAP }
                    .maxByOrNull { it.ap }
                    ?: allAbilities.first()
                ).apply { target = targetHero }

            if (battleId == "farm_battle" && targetHero.character.id == "luana"
                && ability.calculateDamage() >= targetHero.character.currentHp
            ) {
                return AttackAction(currentEnemy, targetHero, ability.toAlwaysIsHitFalse())
            } else {
                return AttackAction(currentEnemy, targetHero, ability)
            }
        }

        private fun BattleAbilityItem.toAlwaysIsHitFalse(): BattleAbilityItem {
            val ability: BattleAbilityItem = this
            return object : BattleAbilityItem(ability.abilityItem, ability.attacker) {
                override fun isHit(): Boolean = false
                override fun possibleCreateCopyWithGrayName(): BattleAbilityItem = this
                override fun createPreviewMessage(): String = ""
                override fun handleSuccess(messages: ArrayDeque<String>) = ability.handleSuccess(messages)
            }
        }
    }

    init {
        selectedAttack.target = currentTarget
    }

    fun isCostingTooMuchApSp(): String? {
        return when {
            !selectedAttack.hasEnoughApSp() -> {
                createPreviewMessage() +
                    System.lineSeparator() +
                    "_________________" +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "[FIREBRICK]Not enough AP/SP!"
            }
            else -> null
        }
    }

    fun isUnableWithCurrentWeapon(): String? {
        return when {
            !selectedAttack.isWeaponAllowed() -> {
                createPreviewMessage() +
                    System.lineSeparator() +
                    "_________________" +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "[FIREBRICK]Unable with current weapon!"
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): String {
        return createPreviewMessage() +
            System.lineSeparator() +
            "_________________" + """

            Attack?"""
    }

    fun createPreviewMessage(): String {
        return selectedAttack.createPreviewMessage()
    }

    fun handle(): ArrayDeque<String>? {
        if (currentParticipant.currentAP < selectedAttack.ap) {
            return null
        }

        currentParticipant.currentAP -= selectedAttack.ap
        attacker.currentSp -= selectedAttack.sp

        val messages = ArrayDeque<String>()
        messages.add("${attacker.name} used ${selectedAttack.name} on ${target.name}.")

        createDebugMessage()
        selectedAttack.handle(messages)
        return messages
    }

    private fun createDebugMessage() {
        if (preferenceManager.isInDebugMode) {
            println("${attacker.name}: " +
                        "${selectedAttack.calculateHitPercentage()}% hit, " +
                        "${selectedAttack.calculateCriticalHitPercentage()}% critHit, " +
                        "${selectedAttack.ap} AP.")
        }
    }

}
