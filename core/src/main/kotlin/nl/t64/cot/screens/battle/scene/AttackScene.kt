package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Character
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.toTexture


private const val HERO_SCALE = 3f
private const val ENEMY_SCALE = 1.75f
private const val ATTACK_ANIMATION_SCALE = 1.5f

private const val GROUND_Y = 300f
private const val HERO_X = 520f
private const val ENEMY_CENTER_X = 1340f

private const val SCENE_FADE_DURATION = 0.2f
private const val PAUSE_AFTER_FADE_IN = 0.7f
private const val PAUSE_BEFORE_FADE_OUT = 0.5f

private const val STEP_DISTANCE = 200f
private const val STEP_DURATION = 0.25f
private const val HIT_MOMENT = HERO_TIME_UNTIL_LAST_FRAME
private const val ATTACK_HOLD_DURATION = 0.5f

private const val ENEMY_WHITEN_DURATION = 0.3f
private const val LUNGE_DISTANCE = 120f
private const val LUNGE_DURATION = 0.1f
private const val LUNGE_BACK_DURATION = 0.25f
private const val REACTION_DURATION = 0.5f

private const val DODGE_DISTANCE = 60f
private const val DODGE_DURATION = 0.1f
private const val DODGE_HOLD_DURATION = 0.2f
private const val DODGE_BACK_DURATION = 0.15f
private const val SHAKE_INTENSITY = 40f
private const val CRITICAL_SHAKE_INTENSITY = 80f
private const val SHAKE_DURATION = 0.3f

private const val HIT_STOP_DURATION = 0.2f
private const val SCREEN_FLASH_ALPHA = 0.8f
private const val SCREEN_FLASH_FADE_DURATION = 0.1f

private const val NUMBER_RISE = 60f
private const val NUMBER_DURATION = 1.5f
private const val NUMBER_FADE_DURATION = 0.1f
// a spell takes longer to build up than a weapon strike, so its number waits until the spell has hit.
private const val SPELL_NUMBER_DELAY = 0.35f
private const val CRITICAL_POP_SCALE = 1.6f
private const val CRITICAL_POP_DURATION = 0.15f

private const val LYING_DURATION = 0.45f
private const val DEATH_DURATION = 0.8f

private val NO_TARGET_FLASH: (Color, Float) -> Unit = { _, _ -> }
// lazy, because a texture can only be made once the game is running.
private val WHITE_TEXTURE: Texture by lazy { Color.WHITE.toTexture() }

class AttackScene(
    private val heroCharacter: Character,
    private val enemyCharacter: Character,
    private val isHeroAttacking: Boolean,
    private val isMelee: Boolean
) : Group() {

    private val heroCombatant = HeroCombatantActor(heroCharacter.id, HERO_SCALE)
    private val enemyCombatant = EnemyCombatantActor(enemyCharacter.id, ENEMY_SCALE)
    private val targetCombatant: CombatantActor = if (isHeroAttacking) enemyCombatant else heroCombatant
    private var hitStopTimeLeft: Float = 0f

    init {
        heroCombatant.setPosition(HERO_X, GROUND_Y)
        enemyCombatant.setPosition(ENEMY_CENTER_X - (enemyCombatant.width / 2f), GROUND_Y)
        addActor(enemyCombatant)
        addActor(heroCombatant)
    }

    // the scene stays on the stage after the attacks, until it is closed.
    fun play(stage: Stage, attackData: List<AttackData>, onFinished: () -> Unit) {
        stage.addActor(this)
        addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(SCENE_FADE_DURATION),
            Actions.delay(PAUSE_AFTER_FADE_IN),
            createStepForward(),
            createAttacks(attackData),
            Actions.run { onFinished.invoke() }
        ))
    }

    fun playNext(attackData: List<AttackData>, onFinished: () -> Unit) {
        addAction(Actions.sequence(
            createAttacks(attackData),
            Actions.run { onFinished.invoke() }
        ))
    }

    fun close(onClosed: () -> Unit) {
        addAction(Actions.sequence(
            Actions.delay(PAUSE_BEFORE_FADE_OUT),
            Actions.fadeOut(SCENE_FADE_DURATION),
            Actions.run { onClosed.invoke() },
            Actions.removeActor()
        ))
    }

    // a weapon switch always starts a new scene, so a scene only knows one kind of attack.
    fun isContinuedBy(hero: Character, enemy: Character, isMeleeAttack: Boolean): Boolean {
        return heroCharacter == hero && enemyCharacter == enemy && isMelee == isMeleeAttack
    }

    // during a hit-stop everything in the scene freezes, including the steps that are still to come.
    override fun act(delta: Float) {
        if (hitStopTimeLeft > 0f) {
            hitStopTimeLeft -= delta
            return
        }
        super.act(delta)
    }

    private fun createStepForward(): Action {
        return if (isHeroAttacking && isMelee) {
            createWalk(STEP_DISTANCE)
        } else {
            Actions.sequence()
        }
    }

    private fun createAttacks(attackData: List<AttackData>): Action {
        val attacksBeforeLast: List<Action> = attackData.dropLast(1).map { createAttack(it, createAttackEnd()) }
        val lastAttack: Action = createAttack(attackData.last(), createStepBack())
        return Actions.sequence(*(attacksBeforeLast + lastAttack).toTypedArray())
    }

    private fun createAttack(singleData: AttackData, attackEnd: Action): Action {
        return Actions.sequence(
            createAttackStart(singleData),
            Actions.run { playImpact(singleData) },
            Actions.parallel(createHoldThen(attackEnd),
                             createWaitForNumber(singleData)),
            createPossibleDeath(singleData)
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

    // an enemy is one still image without motions, so it lights up white first to show that it attacks.
    private fun createEnemyAttackStart(): Action {
        return Actions.sequence(
            Actions.run { enemyCombatant.flash(Color.WHITE, ENEMY_WHITEN_DURATION) },
            Actions.delay(ENEMY_WHITEN_DURATION),
            createPossibleLungeForward()
        )
    }

    private fun playImpact(singleData: AttackData) {
        singleData.getImpactSounds().forEach { playSe(it) }
        possiblePlayAnimation(singleData)
        shakeOrDodgeTarget(singleData)
        possibleReact(singleData)
        possibleStartHitStopWithScreenFlash(singleData)
        showNumberWhenReady(singleData)
    }

    private fun possiblePlayAnimation(singleData: AttackData) {
        val isFailedMagic: Boolean = singleData.isMagic() && !singleData.isHit()
        if (isFailedMagic) return

        val animationId: BattleSceneAnimationId = singleData.getAttackAnimationId()
        val animation = BattleSceneAnimationActor(BattleSceneAnimationDatabase.getAnimation(animationId),
                                                  ATTACK_ANIMATION_SCALE,
                                                  isAttackFromRight = !isHeroAttacking,
                                                  singleData.getTargetFlash())
        animation.setPosition(targetCombatant.centerX, targetCombatant.centerY)
        addActor(animation)
    }

    private fun shakeOrDodgeTarget(singleData: AttackData) {
        if (singleData.isMissed) {
            dodgeTarget()
        } else {
            shakeTarget(singleData)
        }
    }

    private fun dodgeTarget() {
        val awayFromAttacker: Float = if (isHeroAttacking) DODGE_DISTANCE else -DODGE_DISTANCE
        targetCombatant.addAction(Actions.sequence(
            Actions.moveBy(awayFromAttacker, 0f, DODGE_DURATION),
            Actions.delay(DODGE_HOLD_DURATION),
            Actions.moveBy(-awayFromAttacker, 0f, DODGE_BACK_DURATION)
        ))
    }

    private fun shakeTarget(singleData: AttackData) {
        targetCombatant.addAction(ShakeAction(singleData.getShakeIntensity(), SHAKE_DURATION))
    }

    private fun possibleReact(singleData: AttackData) {
        if (isHeroAttacking) return

        heroCombatant.playMotion(singleData.getHeroReactionMotion())
        addAction(Actions.delay(REACTION_DURATION, createHeroWait()))
    }

    private fun possibleStartHitStopWithScreenFlash(singleData: AttackData) {
        if (!singleData.isCriticalHit) return

        hitStopTimeLeft = HIT_STOP_DURATION
        showScreenFlash()
    }

    // the flash is put on the stage instead of in this scene, so it does not freeze with the hit-stop.
    // it is gone quickly, so that the frozen moment itself can be seen.
    private fun showScreenFlash() {
        val screenFlash = Image(WHITE_TEXTURE)
        screenFlash.setSize(stage.width, stage.height)
        screenFlash.color.a = SCREEN_FLASH_ALPHA
        stage.addActor(screenFlash)
        screenFlash.addAction(Actions.sequence(
            Actions.fadeOut(SCREEN_FLASH_FADE_DURATION),
            Actions.removeActor()
        ))
    }

    private fun showNumberWhenReady(singleData: AttackData) {
        addAction(Actions.delay(singleData.getNumberDelay(), Actions.run { showNumber(singleData) }))
    }

    private fun showNumber(singleData: AttackData) {
        val style: NumberStyle = singleData.getNumberStyle()
        val number: Table = createNumber(style, singleData.getNumberText())
        number.setPosition(targetCombatant.centerX - (number.width / 2f), targetCombatant.y + targetCombatant.height)
        val startScale: Float = if (style == NumberStyle.CRITICAL) CRITICAL_POP_SCALE else 1f
        number.setScale(startScale)
        // the fade-in of the rise starts from the current alpha, so the number has to start invisible.
        number.color.a = 0f
        addActor(number)
        number.addAction(createNumberRise())
    }

    private fun createNumber(style: NumberStyle, text: String): Table {
        val number = Table()
        style.label?.let { number.add(Label(it, LabelStyle(FontProvider.barlowOutlined20, style.color))).row() }
        number.add(Label(text, LabelStyle(style.getFont(), style.color)))
        number.pack()
        number.isTransform = true
        number.setOrigin(Align.center)
        return number
    }

    private fun createNumberRise(): Action {
        return Actions.sequence(
            Actions.parallel(Actions.fadeIn(NUMBER_FADE_DURATION), Actions.scaleTo(1f, 1f, CRITICAL_POP_DURATION)),
            Actions.moveBy(0f, NUMBER_RISE, NUMBER_DURATION - CRITICAL_POP_DURATION - NUMBER_FADE_DURATION),
            Actions.fadeOut(NUMBER_FADE_DURATION),
            Actions.removeActor()
        )
    }

    private fun createHoldThen(attackEnd: Action): Action {
        return Actions.sequence(Actions.delay(ATTACK_HOLD_DURATION), attackEnd)
    }

    private fun createWaitForNumber(singleData: AttackData): Action {
        return Actions.delay(singleData.getNumberDelay() + NUMBER_DURATION)
    }

    private fun createPossibleDeath(singleData: AttackData): Action {
        return if (singleData.isTargetDead) {
            createDeath()
        } else {
            Actions.sequence()
        }
    }

    private fun createDeath(): Action {
        return Actions.sequence(
            createPossibleLieDown(),
            Actions.run { playSe(AudioEvent.SE_VANISH) },
            Actions.targeting(targetCombatant, Actions.fadeOut(DEATH_DURATION))
        )
    }

    private fun createPossibleLieDown(): Action {
        return if (isHeroAttacking) {
            Actions.sequence()
        } else {
            Actions.sequence(
                Actions.run { heroCombatant.playMotion(CombatantMotion.DEAD) },
                Actions.delay(LYING_DURATION)
            )
        }
    }

    private fun createAttackEnd(): Action {
        return if (isHeroAttacking) {
            createHeroWait()
        } else {
            createPossibleLungeBack()
        }
    }

    private fun createStepBack(): Action {
        return when {
            !isHeroAttacking -> createPossibleLungeBack()
            isMelee -> createWalk(-STEP_DISTANCE)
            else -> createHeroWait()
        }
    }

    private fun createPossibleLungeForward(): Action {
        return createPossibleLunge(-LUNGE_DISTANCE, LUNGE_DURATION)
    }

    private fun createPossibleLungeBack(): Action {
        return createPossibleLunge(LUNGE_DISTANCE, LUNGE_BACK_DURATION)
    }

    private fun createPossibleLunge(distance: Float, duration: Float): Action {
        return if (isMelee) {
            Actions.targeting(enemyCombatant, Actions.moveBy(distance, 0f, duration))
        } else {
            Actions.sequence()
        }
    }

    private fun createWalk(distance: Float): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(CombatantMotion.WALK) },
            Actions.targeting(heroCombatant, Actions.moveBy(distance, 0f, STEP_DURATION)),
            createHeroWait()
        )
    }

    private fun createHeroWait(): Action {
        return Actions.run { heroCombatant.playMotion(CombatantMotion.WAIT) }
    }

    // a staff only casts spells, and a hero always attacks with a weapon. so the thrust is left for the spear
    // and the dagger.
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

    private fun AttackData.getShakeIntensity(): Float {
        return if (this.isCriticalHit) CRITICAL_SHAKE_INTENSITY else SHAKE_INTENSITY
    }

    private fun AttackData.getNumberDelay(): Float {
        return if (this.isMagic() && this.isHit()) SPELL_NUMBER_DELAY else 0f
    }

    private fun AttackData.getNumberStyle(): NumberStyle {
        return when {
            !this.isHit() -> NumberStyle.MISS_OR_BLOCK
            this.isCriticalHit -> NumberStyle.CRITICAL
            this.hasAdvantage -> NumberStyle.ADVANTAGE
            this.hasDisadvantage -> NumberStyle.DISADVANTAGE
            else -> NumberStyle.NORMAL
        }
    }

    private fun AttackData.getNumberText(): String {
        return when {
            this.isMissed -> "Miss"
            this.isBlocked -> "Block"
            else -> "${this.damage}"
        }
    }

    private fun AttackData.getTargetFlash(): (Color, Float) -> Unit {
        return if (this.isHit()) targetCombatant::flash else NO_TARGET_FLASH
    }

    // the flat streak of the thrust is used for the bow and throwing as well.
    private fun AttackData.getAttackAnimationId(): BattleSceneAnimationId {
        return when {
            this.isFire() -> BattleSceneAnimationId.FIRE
            this.isWind() -> BattleSceneAnimationId.WIND
            this.isThunder() -> BattleSceneAnimationId.THUNDER
            this.isScratch() -> BattleSceneAnimationId.CLAW
            this.isBite() || this.isBodySlam() -> BattleSceneAnimationId.HIT
            this.isSwung() -> BattleSceneAnimationId.SWING
            else -> BattleSceneAnimationId.THRUST
        }
    }

    private fun AttackData.getImpactSounds(): List<AudioEvent> {
        return when {
            this.isMissed -> listOf(this.getMissSound())
            this.isBlocked -> listOf(AudioEvent.SE_BLOCK)
            this.isCriticalHit -> this.getCriticalSounds()
            this.hasAdvantage -> listOf(this.getElementSound() ?: AudioEvent.SE_DAMAGE_ADVANTAGE)
            this.hasDisadvantage -> listOf(this.getElementSound() ?: AudioEvent.SE_DAMAGE_DISADVANTAGE)
            else -> listOf(this.getElementSound() ?: AudioEvent.SE_DAMAGE_REGULAR)
        }
    }

    private fun AttackData.getMissSound(): AudioEvent {
        return if (this.isMagic()) {
            AudioEvent.SE_MAGIC_MISS
        } else {
            AudioEvent.SE_DODGE
        }
    }

    private fun AttackData.getCriticalSounds(): List<AudioEvent> {
        val elementSound: AudioEvent? = this.getElementSound()
        return if (elementSound != null) {
            listOf(elementSound, AudioEvent.SE_MAGIC_CRIT)
        } else {
            listOf(AudioEvent.SE_CRIT_HIT)
        }
    }

    private fun AttackData.getElementSound(): AudioEvent? {
        return when {
            this.isFire() -> AudioEvent.SE_DAMAGE_FIRE
            this.isWind() -> AudioEvent.SE_DAMAGE_WIND
            this.isThunder() -> AudioEvent.SE_DAMAGE_THUNDER
            else -> null
        }
    }

}
