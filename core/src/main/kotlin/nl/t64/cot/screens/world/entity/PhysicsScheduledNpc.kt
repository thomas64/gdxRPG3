package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.OnActionEvent
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


class PhysicsScheduledNpc : PhysicsComponent() {

    lateinit var conversationId: String
    private var isSelected = false

    init {
        boundingBoxWidthPercentage = 0.80f
        boundingBoxHeightPercentage = 0.30f
    }

    override fun receive(event: Event) {
        if (event is UpdateScheduledEntityEvent) {
            state = event.state
            currentPosition = event.position
            direction = event.direction
            conversationId = event.conversationId
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            if (event.checkRect.overlaps(boundingBox)) {
                isSelected = true
            }
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (isSelected) {
            isSelected = false
            brokerManager.componentObservers.notifyShowConversationDialog(conversationId, entity)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
    }

}
