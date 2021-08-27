package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.AnimationType


class GameMapSparkle(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    val name: String = rectObject.name
    val animationType: AnimationType = createAnimationType(rectObject)

    private fun createAnimationType(rectObject: RectangleMapObject): AnimationType {
        return when (rectObject.type) {
            null -> AnimationType.LONG
            "short" -> AnimationType.SHORT
            "none" -> AnimationType.NONE
            else -> throw IllegalArgumentException("RectObject.type: ${rectObject.type} is not usable.")
        }
    }

}
