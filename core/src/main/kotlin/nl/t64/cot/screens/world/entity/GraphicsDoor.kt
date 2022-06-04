package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils
import nl.t64.cot.components.door.Door
import nl.t64.cot.components.door.DoorType
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


class GraphicsDoor(private val door: Door) : GraphicsComponent() {

    private val openAnimation: Animation<TextureRegion> = loadAnimation(false)
    private val shadowAnimation: Animation<TextureRegion>? =
        if (door.type == DoorType.LARGE_WOODEN_CAVE_GATE) loadAnimation(true) else null
    private var shadowFrame: TextureRegion? = null

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            position = event.position
            state = event.state!!
        }
        if (event is StateEvent) {
            state = event.state
        }
    }

    override fun update(dt: Float) {
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
            frameTime >= 1f -> 1f
            state == EntityState.OPENED -> frameTime + dt
            else -> 0f
        }
        currentFrame = openAnimation.getKeyFrame(frameTime)
        shadowFrame = shadowAnimation?.getKeyFrame(frameTime)
    }

    override fun getAnimation(): Animation<TextureRegion> {
        return openAnimation    // todo, doorshadow is not possible in cutscenes this way atm.
    }

    private fun loadAnimation(isShadow: Boolean): Animation<TextureRegion> {
        val textureFrames = Utils.getDoorImage(door.spriteId, door.width.toInt(), isShadow)
        val frames = Array(arrayOf(textureFrames[0][0], textureFrames[1][0], textureFrames[2][0], textureFrames[3][0]))
        return Animation(Constant.FAST_FRAMES, frames, Animation.PlayMode.NORMAL)
    }

}
