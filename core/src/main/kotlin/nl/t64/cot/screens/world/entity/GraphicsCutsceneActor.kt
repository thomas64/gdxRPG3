package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import nl.t64.cot.constants.Constant


class GraphicsCutsceneActor(spriteId: String) : GraphicsNpc(spriteId) {

    override fun getAnimation(): Animation<TextureRegion> {
        setNewFrameDuration(0f) // getFrameDuration override uses state, not moveSpeed
        return when (direction) {
            Direction.NORTH -> walkNorthAnimation
            Direction.SOUTH -> walkSouthAnimation
            Direction.WEST -> walkWestAnimation
            Direction.EAST -> walkEastAnimation
            else -> throw IllegalArgumentException("No animation for other directions.")
        }
    }

    override fun getFrameDuration(moveSpeed: Float): Float {
        return when (state) {
            EntityState.CRAWLING -> Constant.SLOW_FRAMES
            EntityState.WALKING -> Constant.NORMAL_FRAMES
            EntityState.RUNNING -> Constant.FAST_FRAMES
            else -> frameDuration
        }
    }

}
