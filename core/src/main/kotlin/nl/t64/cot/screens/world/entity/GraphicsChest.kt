package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


class GraphicsChest : GraphicsComponent() {

    private val chestImage: List<TextureRegion> = Utils.getChestImage()

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
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

    override fun setFrame(dt: Float) {
        currentFrame = if (state == EntityState.OPENED) getOpenFrame() else getClosedFrame()
    }

    private fun getOpenFrame(): TextureRegion = chestImage[1]
    private fun getClosedFrame(): TextureRegion = chestImage[0]

}
