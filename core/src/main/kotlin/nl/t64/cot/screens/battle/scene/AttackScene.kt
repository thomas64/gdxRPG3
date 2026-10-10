package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Character


private const val HERO_SCALE = 3f
private const val ENEMY_SCALE = 3.75f
private const val ATTACK_ANIMATION_SCALE = 1.5f

private const val GROUND_Y = 300f
private const val HERO_X = 520f
private const val ENEMY_CENTER_X = 1340f
private const val STEP_DISTANCE = 200f
private const val LUNGE_DISTANCE = 120f

private const val SCENE_FADE_DURATION = 0.2f
private const val PAUSE_AFTER_FADE_IN = 0.7f
private const val PAUSE_BEFORE_FADE_OUT = 0.5f
private const val STEP_DURATION = 0.25f
private const val HIT_MOMENT = HERO_TIME_UNTIL_LAST_FRAME
private const val ATTACK_HOLD_DURATION = 0.5f
private const val ENEMY_WHITEN_DURATION = 0.3f
private const val LUNGE_DURATION = 0.1f
private const val LUNGE_BACK_DURATION = 0.25f
private const val REACTION_DURATION = 0.5f

class AttackScene(
    heroCharacter: Character,
    enemyCharacter: Character,
    private val isHeroAttacking: Boolean,
    private val attackData: List<AttackData>,
    private val onFinished: () -> Unit
) : Group() {

    private val heroCombatant = HeroCombatantActor(heroCharacter.id, HERO_SCALE)
    private val enemyCombatant = EnemyCombatantActor(enemyCharacter.id, ENEMY_SCALE)
    private val targetCombatant: CombatantActor = if (isHeroAttacking) enemyCombatant else heroCombatant
    private val isMelee: Boolean = attackData.first().isMelee()

    init {
        heroCombatant.setPosition(HERO_X, GROUND_Y)
        enemyCombatant.setPosition(ENEMY_CENTER_X - (enemyCombatant.width / 2f), GROUND_Y)
        addActor(enemyCombatant)
        addActor(heroCombatant)
    }

    fun play(stage: Stage) {
        stage.addActor(this)
        addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(SCENE_FADE_DURATION),
            Actions.delay(PAUSE_AFTER_FADE_IN),
            createStepForward(),
            createAttacks(),
            createStepBack(),
            Actions.delay(PAUSE_BEFORE_FADE_OUT),
            Actions.fadeOut(SCENE_FADE_DURATION),
            Actions.run { onFinished.invoke() },
            Actions.removeActor()
        ))
    }

    private fun createStepForward(): Action {
        return if (isHeroAttacking && isMelee) {
            createWalk(STEP_DISTANCE)
        } else {
            Actions.sequence()
        }
    }

    private fun createAttacks(): Action {
        val attacks: Array<Action> = attackData.map { createAttack(it) }.toTypedArray()
        return Actions.sequence(*attacks)
    }

    private fun createAttack(singleData: AttackData): Action {
        return Actions.sequence(
            createAttackStart(singleData),
            Actions.run { playImpact(singleData) },
            Actions.delay(ATTACK_HOLD_DURATION),
            createAttackEnd()
        )
    }

    private fun createAttackStart(singleData: AttackData): Action {
        return if (isHeroAttacking) {
            createHeroAttackStart(singleData)
        } else {
            createEnemyAttackStart()
        }
    }

    private fun createHeroAttackStart(singleData: AttackData): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(singleData.getHeroAttackMotion()) },
            Actions.delay(HIT_MOMENT)
        )
    }

    private fun createEnemyAttackStart(): Action {
        return Actions.sequence(
            Actions.run { enemyCombatant.flash(Color.WHITE, ENEMY_WHITEN_DURATION) },
            Actions.delay(ENEMY_WHITEN_DURATION),
            createPossibleLunge(-LUNGE_DISTANCE, LUNGE_DURATION)
        )
    }

    private fun playImpact(singleData: AttackData) {
        possiblePlayAnimation(singleData)
        possibleReact(singleData)
    }

    private fun possiblePlayAnimation(singleData: AttackData) {
        val isFailedMagic: Boolean = singleData.isMagic() && (singleData.isMissed || singleData.isBlocked)
        if (isFailedMagic) return

        val animationId: AttackAnimationId = singleData.getAttackAnimationId()
        val animation = AttackAnimationActor(AttackAnimationDatabase.getAnimation(animationId),
                                             ATTACK_ANIMATION_SCALE,
                                             isAttackFromRight = !isHeroAttacking)
        animation.setPosition(targetCombatant.centerX, targetCombatant.centerY)
        addActor(animation)
    }

    private fun possibleReact(singleData: AttackData) {
        if (isHeroAttacking) return

        heroCombatant.playMotion(singleData.getHeroReactionMotion())
        addAction(Actions.delay(REACTION_DURATION, Actions.run {
            heroCombatant.playMotion(singleData.getHeroMotionAfterReaction())
        }))
    }

    private fun createAttackEnd(): Action {
        return if (isHeroAttacking) {
            Actions.sequence()
        } else {
            createPossibleLunge(LUNGE_DISTANCE, LUNGE_BACK_DURATION)
        }
    }

    private fun createPossibleLunge(distance: Float, duration: Float): Action {
        return if (isMelee) {
            Actions.targeting(enemyCombatant, Actions.moveBy(distance, 0f, duration))
        } else {
            Actions.sequence()
        }
    }

    private fun createStepBack(): Action {
        return when {
            !isHeroAttacking -> Actions.sequence()
            isMelee -> createWalk(-STEP_DISTANCE)
            else -> Actions.run { heroCombatant.playMotion(CombatantMotion.WAIT) }
        }
    }

    private fun createWalk(distance: Float): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(CombatantMotion.WALK) },
            Actions.targeting(heroCombatant, Actions.moveBy(distance, 0f, STEP_DURATION)),
            Actions.run { heroCombatant.playMotion(CombatantMotion.WAIT) }
        )
    }

    private fun AttackData.getHeroAttackMotion(): CombatantMotion {
        return when {
            this.isMagic() -> CombatantMotion.SPELL
            this.isRanged() -> CombatantMotion.RANGE
            this.isSwung() -> CombatantMotion.SWING
            else -> CombatantMotion.THRUST
        }
    }

    private fun AttackData.getHeroReactionMotion(): CombatantMotion {
        return when {
            this.isMissed -> CombatantMotion.EVADE
            this.isBlocked -> CombatantMotion.GUARD
            else -> CombatantMotion.DAMAGE
        }
    }

    private fun AttackData.getHeroMotionAfterReaction(): CombatantMotion {
        return if (this.isTargetDead) {
            CombatantMotion.DEAD
        } else {
            CombatantMotion.WAIT
        }
    }

    private fun AttackData.getAttackAnimationId(): AttackAnimationId {
        return when {
            this.isFire() -> AttackAnimationId.FIRE
            this.isWind() -> AttackAnimationId.WIND
            this.isThunder() -> AttackAnimationId.THUNDER
            this.isScratch() -> AttackAnimationId.CLAW
            this.isBite() || this.isBodySlam() -> AttackAnimationId.HIT
            this.isSwung() -> AttackAnimationId.SWING
            else -> AttackAnimationId.THRUST
        }
    }

}
