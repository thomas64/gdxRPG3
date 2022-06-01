package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


class GraphicsNpc(spriteId: String) : GraphicsComponent() {

    init {
        frameDuration = Constant.NORMAL_FRAMES
        loadWalkingAnimation(spriteId)
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            direction = event.direction!!
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is PositionEvent) {
            position = event.position
        }
        if (event is SpeedEvent) {
            setNewFrameDuration(event.moveSpeed)
        }
    }

    override fun update(dt: Float) {
        setFrame(dt)
    }

    override fun render(batch: Batch) {
        if (state == EntityState.INVISIBLE) return
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        if (state != EntityState.INVISIBLE) {
            shapeRenderer.color = with(entity.getConversationId()) {
                when {
                    containsAnyOf("shop") -> Color.GOLD
                    containsAnyOf("academy") -> Color.ROYAL
                    containsAnyOf("school") -> Color.TEAL
                    containsAnyOf("heal", "inn") -> Color.LIME
                    containsAnyOf("priest", "save") -> Color.SALMON
                    else -> return
                }
            }
            shapeRenderer.drawCircle()
        }
    }

    private fun String.containsAnyOf(vararg strings: String): Boolean {
        return strings.any { it in this }
    }

    private fun ShapeRenderer.drawCircle() {
        circle(position.x + Constant.HALF_TILE_SIZE,
               position.y + Constant.HALF_TILE_SIZE,
               Constant.HALF_TILE_SIZE)
    }

}
