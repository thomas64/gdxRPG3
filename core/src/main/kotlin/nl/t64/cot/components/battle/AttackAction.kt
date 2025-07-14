package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.removeColorCoding


open class AttackAction(
    protected val currentParticipant: Participant,
    protected val currentTarget: Participant,
    protected val selectedAbility: BattleAbilityItem,
) {
    protected val instigator: Character = currentParticipant.character
    protected val target: Character = currentTarget.character

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant, battleId: String): AttackAction {
            val allAbilities: List<BattleAbilityItem> = currentEnemy.getBattleAbilities()
            val ability: BattleAbilityItem = (
                allAbilities
                    .filter { it.ap <= currentEnemy.currentAP }
                    .maxByOrNull { it.ap }
                    ?: allAbilities.first()
                ).apply { target = targetHero }

            if (battleId.contains("farm_battle") && targetHero.character.id == "luana"
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
                override fun createCopyForPreview(): BattleAbilityItem = this
                override fun createPreviewMessage(): String = ""
                override fun handleSuccess(attackData: AttackData) = ability.handleSuccess(attackData)
            }
        }
    }

    init {
        selectedAbility.target = currentTarget
    }

    fun isCostingTooMuchApSp(): String? {
        return when {
            !selectedAbility.hasEnoughApSp() -> {
                """${createPreviewMessage()}
                    |_________________
                    |
                    |[FIREBRICK]Not enough AP/SP!""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    fun isUnableWithCurrentWeapon(): String? {
        return when {
            !selectedAbility.isWeaponAllowed() -> {
                """${createPreviewMessage()}
                    |_________________
                    |
                    |[FIREBRICK]Unable with current weapon!""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): Triple<String, String, String> {
        val message = createPreviewMessage()
        val underscores: String = createUnderscoresWithLengthOf(message)
        return Triple(message, "", """

            $underscores

            ${createCommand()}?""".trimIndent()
        )
    }

    open fun createCommand(): String {
        return "Attack"
    }

    fun createPreviewMessage(): String {
        return selectedAbility.createPreviewMessage()
    }

    open fun handle(): List<AttackData> {
        if (currentParticipant.currentAP < selectedAbility.ap) {
            return emptyList()
        }

        currentParticipant.currentAP -= selectedAbility.ap
        instigator.currentSp -= selectedAbility.sp

        createDebugMessage()
        return selectedAbility.handle()
    }

    private fun createDebugMessage() {
        if (preferenceManager.isInDebugMode) {
            println("${instigator.name}: " +
                        "${selectedAbility.calculateHitPercentage()}% hit, " +
                        "${selectedAbility.calculateCriticalHitPercentage()}% critHit, " +
                        "${selectedAbility.ap} AP.")
        }
    }

    private fun createUnderscoresWithLengthOf(allLines: String): String {
        val minLength = 17
        val maxLength = allLines.getLongestLineLength()
        return "_".repeat(maxOf(minLength, maxLength))
    }

    private fun String.getLongestLineLength(): Int {
        return this.lines()
            .map { it.removeColorCoding().trim() }
            .maxOf { it.length }
    }

}
