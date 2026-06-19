package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant


private const val STATES_PATH: String = "sprites/states.png"
private const val CELL_SIZE: Int = 96
private const val ICON_SIZE: Float = 96f

enum class StateIcon(val row: Int) {
    TALK(2),
}

class OverheadIcon(
    stateIcon: StateIcon
) {
    private var frameTime: Float = 0f
    private val animation: Animation<TextureRegion> = createAnimation(stateIcon)

    companion object {
        private val statesSplit: Array<Array<TextureRegion>> by lazy {
            Utils.getSplitTexture(STATES_PATH, CELL_SIZE, CELL_SIZE)
        }
    }

    fun update(dt: Float) {
        frameTime += dt
    }

    fun render(batch: Batch, position: Vector2) {
        val frame: TextureRegion = animation.getKeyFrame(frameTime)
        val x: Float = position.x + (Constant.TILE_SIZE / 2f) - (ICON_SIZE / 2f)
        val y: Float = position.y
        batch.draw(frame, x, y, ICON_SIZE, ICON_SIZE)
    }

    private fun createAnimation(stateIcon: StateIcon): Animation<TextureRegion> {
        val frames: GdxArray<TextureRegion> = statesSplit[stateIcon.row].toGdxArray()
        return Animation(Constant.NORMAL_FRAMES, frames, Animation.PlayMode.LOOP)
    }

}
