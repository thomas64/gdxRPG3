package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import ktx.tiled.property
import ktx.tiled.propertyOrNull
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


open class GameMapNpc(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    val name: String = rectObject.name
    val state: EntityState = createState(rectObject)
    val direction: Direction = createDirection(rectObject)
    val conversation: String = createConversation(rectObject)
    val conditionIds: List<String> = createConditions(rectObject)
    val position: Vector2 get() = Vector2(rectangle.x, rectangle.y)

    private fun createState(rectObject: RectangleMapObject): EntityState {
        val entityState = rectObject.type
        return when {
            entityState == null -> EntityState.IMMOBILE
            entityState.equals("inv", true) -> EntityState.INVISIBLE
            entityState.equals("w", true) -> EntityState.getRandomIdleOrWalking()
            entityState.equals("ia", true) -> EntityState.IDLE_ANIMATING
            entityState.equals("f", true) -> EntityState.FLYING
            else -> throw IllegalArgumentException("EntityState '$entityState' unknown.")
        }
    }

    private fun createDirection(rectObject: RectangleMapObject): Direction {
        val direction = rectObject.propertyOrNull<String>("direction")?.uppercase()
        return direction?.let { Direction.valueOf(it) } ?: Direction.getRandom()
    }

    private fun createConversation(rectObject: RectangleMapObject): String {
        return rectObject.property("conversation", "default")
    }

    private fun createConditions(rectObject: RectangleMapObject): List<String> {
        val condition = rectObject.propertyOrNull<String>("condition")
        return condition?.split(",")?.map { it.trim() } ?: emptyList()
    }

}
