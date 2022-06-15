package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapCutscene(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), CollisionObserver {

    private val cutsceneId: String = rectObject.name
    private val conditionIds: List<String> = createConditions(rectObject)

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle) && ConditionDatabase.isMeetingConditions(conditionIds)) {
            brokerManager.mapObservers.notifyStartCutscene(cutsceneId)
        }
    }

}
