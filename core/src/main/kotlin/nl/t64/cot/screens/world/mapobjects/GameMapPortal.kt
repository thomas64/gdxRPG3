package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.type
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapPortal : GameMapObject, CollisionObserver {

    val fromMapName: String
    val toMapName: String
    val toMapLocation: String
    val fadeColor: Color = Color.BLACK
    lateinit var enterDirection: Direction

    constructor(fromMapName: String, toMapName: String) : this(
        rectObject = RectangleMapObject().apply { name = toMapName },
        fromMapName = fromMapName,
        shouldAddObserver = false
    )

    constructor(mapTitle: String) : this(
        rectObject = RectangleMapObject(),
        fromMapName = mapTitle,
        shouldAddObserver = false
    )

    constructor(rectObject: RectangleMapObject,
                fromMapName: String,
                shouldAddObserver: Boolean = true) : super(rectObject.rectangle) {

        this.fromMapName = fromMapName
        this.toMapName = rectObject.name
        this.toMapLocation = rectObject.type.orEmpty()

        if (shouldAddObserver) {
            brokerManager.collisionObservers.addObserver(this)
        }
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle)) {
            mapManager.collisionPortal(this, playerDirection)
        }
    }

}
