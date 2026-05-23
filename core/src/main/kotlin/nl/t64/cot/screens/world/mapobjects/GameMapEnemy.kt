package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property
import ktx.tiled.propertyOrNull
import nl.t64.cot.components.condition.ConditionDatabase


class GameMapEnemy(rectObject: RectangleMapObject) : GameMapNpc(rectObject) {

    val battleId: String = rectObject.property("battleId")
    val detectionRange: Int? = rectObject.propertyOrNull<Int>("detectionRange")

    override fun isMeetingConditions(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditions, battleId)
    }

}
