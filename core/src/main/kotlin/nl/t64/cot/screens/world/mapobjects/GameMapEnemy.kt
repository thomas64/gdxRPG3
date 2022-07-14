package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property
import nl.t64.cot.components.condition.ConditionDatabase


class GameMapEnemy(rectObject: RectangleMapObject) : GameMapNpc(rectObject) {

    val battleId: String = rectObject.property("battleId")

    override fun isMeetingConditions(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds, battleId)
    }

}