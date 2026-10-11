package nl.t64.cot.screens.battle.action

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.AttackAction
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.screens.battle.BattleState
import nl.t64.cot.screens.battle.effects.FadeEffect
import nl.t64.cot.screens.battle.scene.AttackScene
import nl.t64.cot.screens.dialog.MessageDialog


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f
private const val DEATH_DELAY = 1.4f

class AttackOutcomeManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val battleFieldTable: () -> Table,
    private val battleState: BattleState
) {

    fun enemyAttackConfirmed(attackData: List<AttackData>) {
        battleState.isDelayingTurn = true
        val hero: Character = attackData.getTarget()
        playEnemyAttackScene(attackData) {
            playOutcomeOnBattleField(attackData, hero)
        }
    }

    fun attackConfirmed(attackAction: AttackAction) {
        val attackData: List<AttackData> = attackAction.handle()
        battleState.isDelayingTurn = true
        val enemy: Character = attackData.getTarget()
        playHeroAttackScene(attackData) {
            playOutcomeOnBattleField(attackData, enemy)
        }
    }

    private fun playEnemyAttackScene(attackData: List<AttackData>, onFinished: () -> Unit) {
        val enemy: Character = attackData.getAttacker()
        val hero: Character = attackData.getTarget()
        AttackScene(hero, enemy, isHeroAttacking = false, attackData, onFinished).play(stage)
    }

    private fun playHeroAttackScene(attackData: List<AttackData>, onFinished: () -> Unit) {
        val hero: Character = attackData.getAttacker()
        val enemy: Character = attackData.getTarget()
        AttackScene(hero, enemy, isHeroAttacking = true, attackData, onFinished).play(stage)
    }

    // the target is known from the start, because it may already be removed from the battle when the messages show.
    private fun playOutcomeOnBattleField(attackData: List<AttackData>, target: Character) {
        attackData.playEffectsSequentially()
        Utils.runWithDelay(attackData.getDelay()) {
            attackData.possibleShowMessagesAndSetDelayingTurnToFalse(target)
        }
    }

    private fun List<AttackData>.getAttacker(): Character {
        return turnManager.getParticipant(this.first().attacker).character
    }

    private fun List<AttackData>.getTarget(): Character {
        return turnManager.getParticipant(this.first().target).character
    }

    private fun List<AttackData>.playEffectsSequentially(index: Int = 0) {
        if (index >= this.size) return

        val singleData = this[index]
        singleData.possiblePlayDeath()

        val delay: Float = if (index == 0 && this.size > 1) singleData.getDelay().toFloat() + 0.2f else 0f
        Utils.runWithDelay(delay) {
            playEffectsSequentially(index + 1)
        }
    }

    private fun AttackData.possiblePlayDeath() {
        if (!this.isTargetDead) return

        Utils.runWithDelay(DEATH_DELAY) {
            playSe(AudioEvent.SE_VANISH)
            FadeEffect(battleFieldTable.invoke(), this.target).start()
            turnManager.removeKilledParticipants()
        }
    }

    private fun List<AttackData>.getDelay(): Float {
        return this.sumOf { it.getDelay() }.toFloat()
    }

    private fun AttackData.getDelay(): Double {
        return when {
            isMissed -> DEFAULT_FLOATING_NUMBER_DELAY.toDouble()
            isBlocked -> DEFAULT_FLOATING_NUMBER_DELAY.toDouble()
            isTargetDead -> 3.5
            else -> 1.6
        }
    }

    private fun List<AttackData>.possibleShowMessagesAndSetDelayingTurnToFalse(target: Character) {
        val (messages, audioEvent) = possibleCreateMessagesFrom(this, target)
        if (messages.isNotEmpty()) {
            val messageDialog = MessageDialog(messages)
            messageDialog.setActionAfterHide {
                battleState.isDelayingTurn = false
            }
            messageDialog.show(stage, audioEvent)
        } else {
            battleState.isDelayingTurn = false
        }
    }

    private fun possibleCreateMessagesFrom(attackData: List<AttackData>, target: Character): Pair<String, AudioEvent> {
        val isEnemyStaggeredMessage: String? = attackData.getPossibleStaggerMessage()
        val attackerWeaponBrokeMessage: String? = attackData.firstNotNullOfOrNull { it.attackerWeaponBrokeMessage }
        val attackerShieldBrokeMessage: String? = attackData.firstNotNullOfOrNull { it.attackerShieldBrokeMessage }
        val targetShieldBrokeMessage: String? = attackData.firstNotNullOfOrNull { it.targetShieldBrokeMessage }
        val isHeroShieldBroken: Boolean = targetShieldBrokeMessage != null && target is HeroItem

        val messages: String = listOfNotNull(
            isEnemyStaggeredMessage,
            attackerWeaponBrokeMessage,
            attackerShieldBrokeMessage,
            targetShieldBrokeMessage
        ).joinToString(System.lineSeparator())
        val audioEvent: AudioEvent = if (attackerWeaponBrokeMessage != null || attackerShieldBrokeMessage != null || isHeroShieldBroken) {
            AudioEvent.SE_WEAPON_BREAK
        } else {
            AudioEvent.SE_CONVERSATION_NEXT
        }
        return messages to audioEvent
    }

    private fun List<AttackData>.getPossibleStaggerMessage(): String? {
        return when {
            this.all { it.isStaggered == null } -> {
                null
            }
            this.any { it.isStaggered == true } && this.none { it.isTargetDead } -> {
                "${this[0].target} was successfully staggered!"
            }
            this.any { it.isStaggered == false } && this.none { it.isTargetDead } -> {
                "The attack failed to stagger ${this[0].target}..."
            }
            else -> {
                null
            }
        }
    }

}
