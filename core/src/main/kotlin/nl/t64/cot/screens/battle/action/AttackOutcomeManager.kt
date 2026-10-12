package nl.t64.cot.screens.battle.action

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.battle.AttackAction
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.screens.battle.BattleState
import nl.t64.cot.screens.battle.scene.AttackScene
import nl.t64.cot.screens.dialog.MessageDialog


class AttackOutcomeManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val battleState: BattleState
) {

    private var openEnemyAttackScene: AttackScene? = null

    fun enemyAttackConfirmed(attackData: List<AttackData>) {
        battleState.isDelayingTurn = true
        val enemy: Character = attackData.getAttacker()
        val hero: Character = attackData.getTarget()
        val isMelee: Boolean = attackData.first().isMelee()
        val onFinished: () -> Unit = { finishEnemyAttack(attackData, hero) }
        val openScene: AttackScene? = openEnemyAttackScene
        if (openScene != null && openScene.isContinuedBy(hero, enemy, isMelee)) {
            openScene.playNext(attackData, onFinished)
        } else {
            closeEnemyAttackScene {
                val scene = AttackScene(hero, enemy, isHeroAttacking = false, isMelee)
                openEnemyAttackScene = scene
                scene.play(stage, attackData, onFinished)
            }
        }
    }

    fun attackConfirmed(attackAction: AttackAction) {
        val attackData: List<AttackData> = attackAction.handle()
        battleState.isDelayingTurn = true
        val hero: Character = attackData.getAttacker()
        val enemy: Character = attackData.getTarget()
        val scene = AttackScene(hero, enemy, isHeroAttacking = true, attackData.first().isMelee())
        scene.play(stage, attackData) {
            scene.close { finishAttack(possibleCreateMessagesFrom(attackData, enemy)) }
        }
    }

    fun closeEnemyAttackScene(onClosed: () -> Unit) {
        val openScene: AttackScene? = openEnemyAttackScene
        openEnemyAttackScene = null
        if (openScene == null) {
            onClosed.invoke()
        } else {
            openScene.close(onClosed)
        }
    }

    private fun finishEnemyAttack(attackData: List<AttackData>, hero: Character) {
        val messagesWithSound: Pair<String, AudioEvent> = possibleCreateMessagesFrom(attackData, hero)
        if (attackData.isInterruptingAttacks(messagesWithSound.first)) {
            closeEnemyAttackScene { finishAttack(messagesWithSound) }
        } else {
            finishAttack(messagesWithSound)
        }
    }

    // a message is shown on the battlefield, not in the scene. and a killed hero cannot be attacked again.
    private fun List<AttackData>.isInterruptingAttacks(messages: String): Boolean {
        return this.any { it.isTargetDead } || messages.isNotEmpty()
    }

    private fun finishAttack(messagesWithSound: Pair<String, AudioEvent>) {
        turnManager.removeKilledParticipants()
        possibleShowMessagesAndSetDelayingTurnToFalse(messagesWithSound)
    }

    private fun List<AttackData>.getAttacker(): Character {
        return turnManager.getParticipant(this.first().attacker).character
    }

    private fun List<AttackData>.getTarget(): Character {
        return turnManager.getParticipant(this.first().target).character
    }

    private fun possibleShowMessagesAndSetDelayingTurnToFalse(messagesWithSound: Pair<String, AudioEvent>) {
        val (messages, audioEvent) = messagesWithSound
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

    // the target is known from the start, because a killed target is removed from the battle before the messages show.
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
