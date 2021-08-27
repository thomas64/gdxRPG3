package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.OnActionEvent
import nl.t64.cot.screens.world.entity.events.OnBumpEvent
import nl.t64.cot.screens.world.entity.events.OnDetectionEvent
import nl.t64.cot.subjects.ActionObserver
import nl.t64.cot.subjects.BlockObserver
import nl.t64.cot.subjects.BumpObserver
import nl.t64.cot.subjects.DetectionObserver


class Entity(
    val id: String,
    private val inputComponent: InputComponent,
    private val physicsComponent: PhysicsComponent,
    private val graphicsComponent: GraphicsComponent
) : ActionObserver, BlockObserver, BumpObserver, DetectionObserver {

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        send(OnActionEvent(checkRect, playerDirection, playerPosition))
    }

    override fun getBlockerFor(boundingBox: Rectangle): Rectangle? {
        return physicsComponent.boundingBox.takeIf { boundingBox.overlaps(it) }
    }

    override fun isBlocking(point: Vector2): Boolean {
        return physicsComponent.boundingBox.contains(point)
    }

    override fun onNotifyBump(biggerBoundingBox: Rectangle, checkRect: Rectangle, playerPosition: Vector2) {
        send(OnBumpEvent(biggerBoundingBox, checkRect, playerPosition))
    }

    override fun onNotifyDetection(playerMoveSpeed: Float) {
        send(OnDetectionEvent(playerMoveSpeed))
    }

    fun send(event: Event) {
        inputComponent.receive(event)
        physicsComponent.receive(event)
        graphicsComponent.receive(event)
    }

    fun update(dt: Float) {
        inputComponent.update(this, dt)
        physicsComponent.update(this, dt)
        graphicsComponent.update(dt)
    }

    fun render(batch: Batch) {
        graphicsComponent.render(batch)
    }

    fun renderOnMiniMap(batch: Batch, shapeRenderer: ShapeRenderer) {
        graphicsComponent.renderOnMiniMap(this, batch, shapeRenderer)
    }

    fun debug(shapeRenderer: ShapeRenderer) {
        physicsComponent.debug(shapeRenderer)
    }

    fun dispose() {
        inputComponent.dispose()
        physicsComponent.dispose()
        graphicsComponent.dispose()
    }

    fun resetInput() {
        inputComponent.reset()
    }

    fun getAnimation(): Animation<TextureRegion> =
        graphicsComponent.getAnimation()

    fun getPositionInGrid(): Vector2 =
        Vector2(((position.x + Constant.HALF_TILE_SIZE) / Constant.HALF_TILE_SIZE).toInt().toFloat(),
                ((position.y + (Constant.TILE_SIZE / 8f)) / Constant.HALF_TILE_SIZE).toInt().toFloat())

    val position: Vector2 get() = physicsComponent.currentPosition
    val direction: Direction get() = physicsComponent.direction
    val state: EntityState get() = physicsComponent.state
    val moveSpeed: Float get() = physicsComponent.velocity

    fun isNpc(): Boolean = physicsComponent is PhysicsNpc

    fun getConversationId(): String =
        (physicsComponent as PhysicsNpc).conversationId

}
