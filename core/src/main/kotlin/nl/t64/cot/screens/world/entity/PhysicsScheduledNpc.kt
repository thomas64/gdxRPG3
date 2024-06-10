package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.events.*


private const val DIRECTION_WAITING_TIME = 5f

class PhysicsScheduledNpc : PhysicsComponent() {

    lateinit var conversationId: String
    private var isSelected = false
    private var isBumped = false
    private var directionTime = 0f

    init {
        boundingBoxWidthPercentage = 0.80f
        boundingBoxHeightPercentage = 0.30f
    }

    override fun receive(event: Event) {
        if (event is UpdateScheduledEntityEvent) {
            state = event.state
            currentPosition = event.position
            if (!isBumped && !isSelected) {
                direction = event.direction
            }
            conversationId = event.conversationId
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            if (event.checkRect.overlaps(boundingBox)) {
                direction = currentPosition.turnToPlayer(event.playerPosition, direction)
                isSelected = true
            }
        }
        if (event is OnBumpEvent) {
            if (event.biggerBoundingBox.overlaps(boundingBox) || event.checkRect.overlaps(boundingBox)) {
                direction = currentPosition.turnToPlayer(event.playerPosition, direction)
                isBumped = true
                directionTime = DIRECTION_WAITING_TIME
            }
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (state != EntityState.IDLE) {
            isBumped = false
        }

        if (isBumped || isSelected) {
            entity.send(DirectionEvent(direction))
        }

        if (isBumped) {
            directionTime -= dt
            if (directionTime < 0) {
                isBumped = false
            }
        }

        if (isSelected) {
            isSelected = false
            worldScreen.showConversationDialogFromNpc(conversationId, entity)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
    }

}
