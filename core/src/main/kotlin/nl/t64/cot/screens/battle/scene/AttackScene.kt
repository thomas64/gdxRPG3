package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.components.battle.Character


private const val HERO_SCALE = 3f
private const val ENEMY_SCALE = 3.75f

private const val GROUND_Y = 300f
private const val HERO_X = 520f
private const val ENEMY_CENTER_X = 1340f

private const val SCENE_FADE_DURATION = 0.2f
private const val PAUSE_AFTER_FADE_IN = 0.7f
private const val PAUSE_BEFORE_FADE_OUT = 0.5f

class AttackScene(
    heroCharacter: Character,
    enemyCharacter: Character,
    private val onFinished: () -> Unit
) : Group() {

    private val hero = HeroCombatantActor(heroCharacter.id, HERO_SCALE)
    private val enemy = EnemyCombatantActor(enemyCharacter.id, ENEMY_SCALE)

    init {
        hero.setPosition(HERO_X, GROUND_Y)
        enemy.setPosition(ENEMY_CENTER_X - (enemy.width / 2f), GROUND_Y)
        addActor(enemy)
        addActor(hero)
    }

    fun play(stage: Stage) {
        stage.addActor(this)
        addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(SCENE_FADE_DURATION),
            Actions.delay(PAUSE_AFTER_FADE_IN + PAUSE_BEFORE_FADE_OUT),
            Actions.fadeOut(SCENE_FADE_DURATION),
            Actions.run { onFinished.invoke() },
            Actions.removeActor()
        ))
    }

}
