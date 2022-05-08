package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import nl.t64.cot.components.condition.ConditionDatabase


class GameMapQuestBlockerCondition(
    rectObject: RectangleMapObject,
    private val conditionIds: List<String>
) : GameMapQuestBlocker(rectObject) {

    override fun update() {
        val isMeetingConditions = ConditionDatabase.isMeetingConditions(conditionIds)
        checkBlocker(isMeetingConditions)
    }

}
