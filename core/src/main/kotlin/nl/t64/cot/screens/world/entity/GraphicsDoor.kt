package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import nl.t64.cot.Utils
import nl.t64.cot.components.door.Door
import nl.t64.cot.components.door.DoorType
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.atmosphere.LitSchedule
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


private const val FRAME_COUNT = 4
private const val FRAME_DURATION = Constant.FAST_FRAMES

class GraphicsDoor(
    private val door: Door,
    private val litSchedule: LitSchedule? = null
) : GraphicsComponent() {
    private val openAnimation: Animation<TextureRegion> = loadAnimation(door.spriteId, false)
    private val litAnimation: Animation<TextureRegion>? = litSchedule?.let { loadAnimation(door.litSpriteId, false) }
    private val shadowAnimation: Animation<TextureRegion>? =
        if (door.type == DoorType.LARGE_WOODEN_CAVE_GATE) loadAnimation(door.spriteId, true) else null
    private var shadowFrame: TextureRegion? = null

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            position = event.position
            state = event.state!!
            frameTime = if (state == EntityState.OPENED) ANIMATION_DURATION else 0f
            setFrame(0f)
        }
        if (event is StateEvent) {
            state = event.state
            frameTime = 0f
        }
    }

    override fun update(dt: Float) {
        setPlayMode()
        setFrame(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, door.width, door.height)
        shadowFrame?.let { batch.draw(it, position.x, position.y - (door.height / 2f), door.width, door.height) }
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

    override fun setFrame(dt: Float) {
        frameTime = when {
            frameTime >= ANIMATION_DURATION -> ANIMATION_DURATION
            state == EntityState.OPENED -> frameTime + dt
            state == EntityState.CLOSING -> frameTime + dt
            else -> 0f
        }
        currentFrame = getCurrentAnimation().getKeyFrame(frameTime)
        shadowFrame = shadowAnimation?.getKeyFrame(frameTime)
    }

    override fun getAnimation(): Animation<TextureRegion> {
        return openAnimation    // todo, doorshadow is not possible in cutscenes this way atm.
    }

    private fun setPlayMode() {
        getCurrentAnimation().playMode = when (state) {
            EntityState.CLOSING -> Animation.PlayMode.REVERSED
            else -> Animation.PlayMode.NORMAL
        }
    }

    private fun getCurrentAnimation(): Animation<TextureRegion> {
        return if (isLit()) litAnimation!! else openAnimation
    }

    private fun isLit(): Boolean {
        return litSchedule?.isLit() == true
    }

    private fun loadAnimation(spriteId: String, isShadow: Boolean): Animation<TextureRegion> {
        val textureFrames: Array<Array<TextureRegion>> = Utils.getDoorImage(spriteId, door.width.toInt(), isShadow)
        val frames: GdxArray<TextureRegion> = (0 until FRAME_COUNT).map { textureFrames[it][0] }.toGdxArray()
        return Animation(FRAME_DURATION, frames, Animation.PlayMode.NORMAL)
    }

    companion object {
        const val ANIMATION_DURATION = FRAME_COUNT * FRAME_DURATION
    }

}
