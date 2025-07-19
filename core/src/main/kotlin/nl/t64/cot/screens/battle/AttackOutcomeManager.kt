package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.AttackAction
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.screens.dialog.MessageDialog


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f

class AttackOutcomeManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val battleFieldTable: () -> Table,
    private val setDelayingTurn: (Boolean) -> Unit
) {

    fun enemyAttackConfirmed(attackData: List<AttackData>) {
        setDelayingTurn.invoke(true)
        attackData.playEffectsSequentially()
        Utils.runWithDelay(attackData.getDelay()) {
            setDelayingTurn.invoke(false)
        }
    }

    fun attackConfirmed(attackAction: AttackAction) {
        val attackData: List<AttackData> = attackAction.handle()
        setDelayingTurn.invoke(true)

        Utils.runWithDelay(0.5f) {
            attackData.playEffectsSequentially()
            Utils.runWithDelay(attackData.getDelay()) {
                attackData.possibleShowMessagesAndSetDelayingTurnToFalse()
            }
        }
    }

    private fun List<AttackData>.playEffectsSequentially(index: Int = 0) {
        if (index >= this.size) return

        val singleData = this[index]
        singleData.playEffect()

        val delay: Float = if (index == 0 && this.size > 1) singleData.getDelay().toFloat() + 0.2f else 0f
        Utils.runWithDelay(delay) {
            playEffectsSequentially(index + 1)
        }
    }

    private fun AttackData.playEffect() {
        val battleField = battleFieldTable.invoke()
        when {
            this.isMissed -> {
                playSe(AudioEvent.SE_DODGE)
                FloatingNumberEffect(battleField, this.target, "Miss", Color.WHITE).floatUp()
            }
            this.isBlocked -> {
                playSe(AudioEvent.SE_BLOCK)
                FloatingNumberEffect(battleField, this.target, "Block", Color.WHITE).floatUp()
            }
            this.isCriticalHit -> {
                playDamageEffect(battleField, "${this.damage} !", Color.RED, AudioEvent.SE_CRIT_HIT)
            }
            this.hasAdvantage -> {
                playDamageEffect(battleField, "${this.damage} +", Color.RED, AudioEvent.SE_DAMAGE_ADVANTAGE)
            }
            this.hasDisadvantage -> {
                playDamageEffect(battleField, "${this.damage} -", Color.CORAL, AudioEvent.SE_DAMAGE_DISADVANTAGE)
            }
            else -> {
                playDamageEffect(battleField, "${this.damage}", Color.SCARLET, AudioEvent.SE_DAMAGE_REGULAR)
            }
        }
    }

    private fun AttackData.playDamageEffect(battleField: Table,
                                            damageText: String,
                                            color: Color,
                                            audioEvent: AudioEvent) {
        ShakeEffect(battleField, this.target).start()
        playSe(audioEvent)
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleField, this.target, damageText, color).floatUp()
            if (this.isTargetDead) {
                Utils.runWithDelay(0.9f) {
                    playSe(AudioEvent.SE_VANISH)
                    FadeEffect(battleField, this.target).start()
                    turnManager.removeKilledParticipants()
                }
            }
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

    private fun List<AttackData>.possibleShowMessagesAndSetDelayingTurnToFalse() {
        val (messages, audioEvent) = possibleCreateMessagesFrom(this)
        if (messages.isNotEmpty()) {
            val messageDialog = MessageDialog(messages)
            messageDialog.setActionAfterHide {
                setDelayingTurn.invoke(false)
            }
            messageDialog.show(stage, audioEvent)
        } else {
            setDelayingTurn.invoke(false)
        }
    }

    private fun possibleCreateMessagesFrom(attackData: List<AttackData>): Pair<String, AudioEvent> {
        val isEnemyStaggeredMessage: String? = attackData.getPossibleStaggerMessage()
        val yourWeaponBrokeMessage: String? = attackData.firstNotNullOfOrNull { it.attackerWeaponBrokeMessage }
        val enemyShieldBrokeMessage: String? = attackData.firstNotNullOfOrNull { it.targetShieldBrokeMessage }

        val messages: String = listOfNotNull(
            isEnemyStaggeredMessage,
            yourWeaponBrokeMessage,
            enemyShieldBrokeMessage
        ).joinToString(System.lineSeparator())
        val audioEvent: AudioEvent = if (yourWeaponBrokeMessage != null) {
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
