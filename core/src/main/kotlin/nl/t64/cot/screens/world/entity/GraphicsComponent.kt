package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant


abstract class GraphicsComponent : Component {

    var frameTime = 0f
    var frameDuration = 0f
    lateinit var currentFrame: TextureRegion
    lateinit var state: EntityState
    lateinit var position: Vector2
    lateinit var direction: Direction
    private lateinit var walkNorthAnimation: Animation<TextureRegion>
    private lateinit var walkSouthAnimation: Animation<TextureRegion>
    private lateinit var walkWestAnimation: Animation<TextureRegion>
    private lateinit var walkEastAnimation: Animation<TextureRegion>

    abstract fun update(dt: Float)
    abstract fun render(batch: Batch)
    abstract fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer)

    override fun dispose() {
        // empty
    }

    open fun setFrame(dt: Float) {
        when (state) {
            EntityState.INVISIBLE -> return
            EntityState.IDLE,
            EntityState.ALIGNING,
            EntityState.IMMOBILE -> {
                setCurrentFrame(Constant.NO_FRAMES)
                // the next line sets player always just 1 dt moment before the end of normal stance when standing still.
                frameTime = frameDuration - dt
            }
            EntityState.WALKING,
            EntityState.FLYING,
            EntityState.PLAYING,
            EntityState.IDLE_ANIMATING -> {
                frameTime = (frameTime + dt) % 12 // dividable by 0.15, 0.25 and 0.5, these are player speed frames.
                if (frameDuration == Constant.NO_FRAMES) { // no player animation when high speed moving.
                    setCurrentFrame(Constant.NO_FRAMES)
                } else {
                    setCurrentFrame(frameTime)
                }
            }
            else -> throw IllegalArgumentException("EntityState '$state' not usable.")
        }
    }

    private fun setCurrentFrame(newFrameTime: Float) {
        val frameTime1 = newFrameTime.takeIf { it >= 0 } ?: Constant.NO_FRAMES

        currentFrame = when (direction) {
            Direction.NORTH -> walkNorthAnimation.getKeyFrame(frameTime1)
            Direction.SOUTH -> walkSouthAnimation.getKeyFrame(frameTime1)
            Direction.WEST -> walkWestAnimation.getKeyFrame(frameTime1)
            Direction.EAST -> walkEastAnimation.getKeyFrame(frameTime1)
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    fun setNewFrameDuration(moveSpeed: Float) {
        when (moveSpeed) {
            Constant.MOVE_SPEED_1 -> frameDuration = Constant.SLOW_FRAMES
            Constant.MOVE_SPEED_2 -> frameDuration = Constant.NORMAL_FRAMES
            Constant.MOVE_SPEED_3 -> frameDuration = Constant.FAST_FRAMES
            Constant.MOVE_SPEED_4 -> frameDuration = Constant.NO_FRAMES
        }
        walkNorthAnimation.frameDuration = frameDuration
        walkSouthAnimation.frameDuration = frameDuration
        walkWestAnimation.frameDuration = frameDuration
        walkEastAnimation.frameDuration = frameDuration
    }

    fun loadWalkingAnimation(spriteId: String) {
        val textureFrames = Utils.getCharImage(spriteId)

        val walkSouthFrames = Array(arrayOf(textureFrames[0][1], textureFrames[0][0], textureFrames[0][1], textureFrames[0][2]))
        val walkWestFrames = Array(arrayOf(textureFrames[1][1], textureFrames[1][0], textureFrames[1][1], textureFrames[1][2]))
        val walkEastFrames = Array(arrayOf(textureFrames[2][1], textureFrames[2][0], textureFrames[2][1], textureFrames[2][2]))
        val walkNorthFrames = Array(arrayOf(textureFrames[3][1], textureFrames[3][0], textureFrames[3][1], textureFrames[3][2]))

        walkSouthAnimation = Animation(frameDuration, walkSouthFrames, Animation.PlayMode.LOOP)
        walkWestAnimation = Animation(frameDuration, walkWestFrames, Animation.PlayMode.LOOP)
        walkEastAnimation = Animation(frameDuration, walkEastFrames, Animation.PlayMode.LOOP)
        walkNorthAnimation = Animation(frameDuration, walkNorthFrames, Animation.PlayMode.LOOP)
    }

    open fun getAnimation(): Animation<TextureRegion> {
        if (state == EntityState.RUNNING) {
            setNewFrameDuration(Constant.MOVE_SPEED_3)
        } else {
            setNewFrameDuration(Constant.MOVE_SPEED_2)
        }
        return when (direction) {
            Direction.NORTH -> walkNorthAnimation
            Direction.SOUTH -> walkSouthAnimation
            Direction.WEST -> walkWestAnimation
            Direction.EAST -> walkEastAnimation
            Direction.NONE -> throw IllegalArgumentException("No animation for direction NONE.")
        }
    }

}
