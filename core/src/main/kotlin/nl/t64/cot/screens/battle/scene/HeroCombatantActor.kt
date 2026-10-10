package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.g2d.TextureRegion


private const val HERO_FRAME_SIZE = 64
private const val FRAMES_PER_MOTION = 3
private const val WAIT_FRAME_DURATION = 0.2f

// a looping motion goes back and forth: 0, 1, 2, 1, 0, 1, 2, 1, ...
private val LOOPING_FRAME_ORDER: List<Int> = listOf(0, 1, 2, 1)
private val SINGLE_FRAME_ORDER: List<Int> = listOf(0, 1, 2)

class HeroCombatantActor(
    heroId: String,
    combatantScale: Float
) : CombatantActor(CombatantImages.getHeroRegion(heroId)) {

    private val motion: CombatantMotion = CombatantMotion.WAIT
    private val frameDuration: Float = WAIT_FRAME_DURATION
    private var motionTime: Float = 0f

    init {
        setSize(HERO_FRAME_SIZE * combatantScale, HERO_FRAME_SIZE * combatantScale)
    }

    override fun act(delta: Float) {
        super.act(delta)
        motionTime += delta
    }

    override fun getCurrentRegion(): TextureRegion {
        val column: Int = (motion.column * FRAMES_PER_MOTION) + getCurrentFrameIndex()
        return region.apply {
            setRegion(column * HERO_FRAME_SIZE, motion.row * HERO_FRAME_SIZE, HERO_FRAME_SIZE, HERO_FRAME_SIZE)
        }
    }

    private fun getCurrentFrameIndex(): Int {
        val step: Int = (motionTime / frameDuration).toInt()
        return if (motion.isLooping) {
            getLoopingFrameIndex(step)
        } else {
            getFrameIndexHeldAtLast(step)
        }
    }

    private fun getLoopingFrameIndex(step: Int): Int {
        return LOOPING_FRAME_ORDER[step % LOOPING_FRAME_ORDER.size]
    }

    private fun getFrameIndexHeldAtLast(step: Int): Int {
        return SINGLE_FRAME_ORDER[step.coerceAtMost(SINGLE_FRAME_ORDER.lastIndex)]
    }

}
