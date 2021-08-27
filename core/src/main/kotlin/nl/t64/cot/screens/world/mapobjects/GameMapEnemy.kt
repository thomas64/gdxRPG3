package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property


class GameMapEnemy(rectObject: RectangleMapObject) : GameMapNpc(rectObject) {

    val battleId: String = rectObject.property("battleId")

}