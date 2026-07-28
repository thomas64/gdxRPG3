package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.ResourceType
import nl.t64.cot.removeColorCoding


open class AttackAction(
    protected val currentParticipant: Participant,
    protected val currentTarget: Participant,
    protected val selectedAbility: BattleAbilityItem,
) {
    protected val instigator: Character = currentParticipant.character
    protected val target: Character = currentTarget.character
    protected val requiredResourceName: String = selectedAbility.abilityItem.requiredResource.title

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant, battleId: String): AttackAction {
            val usableAbilities: List<BattleAbilityItem> = currentEnemy.getUsableBattleAbilities()
            val ability: BattleAbilityItem = (
                usableAbilities
                    .filter { it.ap <= currentEnemy.currentAP }
                    .maxByOrNull { it.ap }
                    ?: usableAbilities.first()
                ).apply { target = targetHero }

            if (battleId.contains("farm_battle") && targetHero.character.id == "luana"
                && ability.calculateDamageMinusProtection() >= targetHero.character.currentHp
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
                val previewMessage: String = createPreviewMessage()
                val warningMessage = "[FIREBRICK]Not enough AP/SP!"
                val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage)
                """ |$previewMessage
                    |$underscores
                    |
                    |$warningMessage""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    fun isUnableWithCurrentWeapon(): String? {
        return when {
            !selectedAbility.isWeaponAllowed() -> {
                val previewMessage: String = createPreviewMessage()
                val warningMessage = "[FIREBRICK]Unable with current weapon!"
                val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage)
                """ |$previewMessage
                    |$underscores
                    |
                    |$warningMessage""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    fun isCostingTooMuchResources(): String? {
        return when {
            !currentParticipant.isHero -> null
            !selectedAbility.hasEnoughResources() -> {
                val previewMessage: String = createPreviewMessage()
                val warningMessage = "[FIREBRICK]Not enough ${requiredResourceName}s!"
                val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage)
                """ |$previewMessage
                    |$underscores
                    |
                    |$warningMessage""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): Triple<String, String, String> {
        val message: String = createPreviewMessage()
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

    fun handle(): List<AttackData> {
        if (currentParticipant.currentAP < selectedAbility.ap) {
            return emptyList()
        }
        if (currentParticipant.isHero && !selectedAbility.hasEnoughResources()) {
            return emptyList()
        }

        currentParticipant.currentAP -= selectedAbility.ap
        instigator.currentSp -= selectedAbility.sp
        if (currentParticipant.isHero && selectedAbility.abilityItem.requiredResource != ResourceType.NONE) {
            gameData.inventory.autoRemoveItem(requiredResourceName.lowercase(), 1)
        }

        createDebugMessage()
        return selectedAbility.handle()
    }

    private fun createDebugMessage() {
        if (preferenceManager.isDebugModeOn) {
            println("${instigator.name}: " +
                        "${selectedAbility.calculateHitPercentage()}% hit, " +
                        "${selectedAbility.calculateCriticalHitPercentage()}% critHit, " +
                        "${selectedAbility.ap} AP.")
        }
    }

    protected fun createUnderscoresWithLengthOf(vararg allLines: String): String {
        val minLength = 17
        val maxLength = allLines.getLongestLineLength()
        return "_".repeat(maxOf(minLength, maxLength))
    }

    private fun Array<out String>.getLongestLineLength(): Int {
        return this.flatMap { it.lines() }
            .map { it.removeColorCoding().trim() }
            .maxOf { it.length }
    }

}
