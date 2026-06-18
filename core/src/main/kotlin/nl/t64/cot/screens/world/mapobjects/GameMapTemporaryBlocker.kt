package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver
import nl.t64.cot.subjects.BumpObserver


// todo, naam van de maptitle opslaan bij de rectangle in de savegame.
class GameMapTemporaryBlocker(
    rectObject: RectangleMapObject
) : GameMapObject(rectObject.rectangle), BlockObserver, BumpObserver {

    private val allowedDirection: Direction = createDirection(rectObject)
    private var isActive: Boolean = !gameData.removedMapBlockers.contains(rectangle)

    init {
        if (isActive) {
            registerObservers()
        }
    }

    override fun getBlockerFor(boundingBox: Rectangle, state: EntityState, entityDirection: Direction): Rectangle? {
        return rectangle.takeIf { isActive && boundingBox.overlaps(it) && entityDirection != allowedDirection }
    }

    override fun isBlocking(point: Vector2, state: EntityState): Boolean {
        return isActive && rectangle.contains(point)
    }

    override fun onNotifyBump(biggerBoundingBox: Rectangle,
                              checkRect: Rectangle,
                              playerPosition: Vector2,
                              playerDirection: Direction
    ) {
        if (!isActive) return
        if (!checkRect.overlaps(rectangle)) return
        if (playerDirection != allowedDirection) return

        gameData.removedMapBlockers.add(rectangle)
        isActive = false
        changeBlocker()
    }

    private fun changeBlocker() {
        if (isActive) {
            registerObservers()
        } else {
            unregisterObservers()
        }
        mapManager.setTiledGraph()
    }

    private fun registerObservers() {
        brokerManager.blockObservers.addObserver(this)
        brokerManager.bumpObservers.addObserver(this)
    }

    private fun unregisterObservers() {
        brokerManager.blockObservers.removeObserver(this)
        brokerManager.bumpObservers.removeObserver(this)
    }

    private fun createDirection(rectObject: RectangleMapObject): Direction {
        return rectObject.propertyOrNull<String>("allowedDirection")
            ?.uppercase()?.let { Direction.valueOf(it) } ?: Direction.NONE
    }

}
