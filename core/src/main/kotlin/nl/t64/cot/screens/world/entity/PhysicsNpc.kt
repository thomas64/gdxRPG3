package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


class PhysicsNpc : PhysicsComponent() {

    lateinit var conversationId: String
    private var isSelected = false

    init {
        velocity = Constant.MOVE_SPEED_1
        boundingBoxWidthPercentage = 0.80f
        boundingBoxHeightPercentage = 0.30f
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            initNpc(event)
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is OnActionEvent) {
            if (event.checkRect.overlaps(boundingBox)) {
                isSelected = true
                entity.send(WaitEvent(currentPosition, event.playerPosition))
            }
        }
        if (event is OnBumpEvent) {
            if (event.biggerBoundingBox.overlaps(boundingBox) || event.checkRect.overlaps(boundingBox)) {
                entity.send(WaitEvent(currentPosition, event.playerPosition))
            }
        }
    }

    private fun initNpc(loadEvent: LoadEntityEvent) {
        state = loadEvent.state!!
        currentPosition = loadEvent.position
        direction = loadEvent.direction!!
        conversationId = loadEvent.conversationOrBattleId!!
        setWanderBox()
        setBoundingBox()
    }

    override fun update(entity: Entity, dt: Float) {
        this.entity = entity
        relocate(dt)
        checkObstacles()
        entity.send(PositionEvent(currentPosition))
        if (isSelected) {
            isSelected = false
            brokerManager.componentObservers.notifyShowConversationDialog(conversationId, entity)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.PURPLE
        if (state != EntityState.IMMOBILE
            && state != EntityState.IDLE_ANIMATING
        ) {
            shapeRenderer.rect(wanderBox.x, wanderBox.y, wanderBox.width, wanderBox.height)
        }
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
    }

}
