package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import kotlin.random.Random


class AttackAction(
    private val currentParticipant: Participant,
    private val target: Character,
    private val selectedAttack: BattleAbilityItem,
) {
    private val attacker: Character = currentParticipant.character

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant, battleId: String): AttackAction {

            val ability: BattleAbilityItem = (
                currentEnemy.getBattleAbilities()
                    .filter { it.ap <= currentEnemy.currentAP }
                    .maxByOrNull { it.ap }
                    ?: currentEnemy.getBattleAbilities().first()
                ).apply { target = targetHero.character }

            if (battleId == "farm_battle" && targetHero.character.id == "luana"
                && ability.calculateDamage() >= targetHero.character.currentHp
            ) {
                return AttackAction(currentEnemy, targetHero.character, ability.toAlwaysIsHitFalse())
            } else {
                return AttackAction(currentEnemy, targetHero.character, ability)
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
        selectedAttack.target = target
    }

    fun isCostingTooMuchAp(): String? {
        return when {
            selectedAttack.ap > currentParticipant.currentAP -> {
                createPreviewMessage() +
                    System.lineSeparator() +
                    "_________________" +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "    Not enough AP!"
            }
            else -> null
        }
    }

    fun isCostingTooMuchSp(): String? {
        return when {
            selectedAttack.sp > attacker.currentSp -> {
                createPreviewMessage() +
                    System.lineSeparator() +
                    "_________________" +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "    Not enough SP!"
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

    fun handle(): ArrayDeque<String> {
        if (currentParticipant.currentAP < selectedAttack.ap) {
            return ArrayDeque(listOf("${attacker.name} ended ${attacker.gender} turn."))
        }

        currentParticipant.currentAP -= selectedAttack.ap
        attacker.currentSp -= selectedAttack.sp

        val messages = ArrayDeque<String>()
        messages.add("${attacker.name} used ${selectedAttack.name} on ${target.name}.")

        if (selectedAttack.isHit()) {
            handleSuccess(messages)
        } else {
            handleFailure(messages)
        }
        createDebugMessage()
        return messages
    }

    private fun handleSuccess(messages: ArrayDeque<String>) {
        val isBlock: Boolean = target.getCalculatedTotalDefense() > Random.nextInt(0, 100)
        if (isBlock) {
            handleBlock(messages)
        } else {
            selectedAttack.handleSuccess(messages)
        }

        handleDurability(messages)

        if (target.isDead) {
            messages.add("${target.name} is defeated.")
        }
    }

    private fun handleFailure(messages: ArrayDeque<String>) {
        messages.add("${attacker.name}'s attack failed.")
    }

    private fun handleBlock(messages: ArrayDeque<String>) {
        messages.add("${target.name} blocked the attack.")
        val shield: InventoryItem = target.getInventoryItem(InventoryGroup.SHIELD)!!
        shield.durability--
        if (shield.durability <= 0) {
            messages.add("${shield.name} broke!")
            target.clearInventoryItemFor(InventoryGroup.SHIELD)
        }
    }

    private fun handleDurability(messages: ArrayDeque<String>) {
        val weapon: InventoryItem = selectedAttack.currentWeapon!!
        weapon.durability--
        if (currentParticipant.isHero && weapon.durability <= 0) {
            messages.add("${weapon.name} broke!")
            attacker.clearInventoryItemFor(InventoryGroup.WEAPON)
        }
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
