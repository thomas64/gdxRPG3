package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.condition.areAllTrue
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver
import nl.t64.cot.subjects.BumpObserver


class GameMapTemporaryBlocker(
    private val mapTitle: String,
    rectObject: RectangleMapObject
) : GameMapObject(rectObject.rectangle), BlockObserver, BumpObserver {

    private val conditions: List<String> = createConditions(rectObject)
    private val allowedDirection: Direction = createDirection(rectObject)
    private val isUnlocked: Boolean get() = gameData.removedBlockers.contains(mapTitle, rectangle)
    private val shouldBeActive: Boolean get() = !isUnlocked && conditions.areAllTrue()
    private var isActive: Boolean = shouldBeActive

    init {
        if (!isUnlocked) {
            brokerManager.bumpObservers.addObserver(this)
        }
        if (isActive) {
            brokerManager.blockObservers.addObserver(this)
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
        if (!checkRect.overlaps(rectangle)) return
        if (playerDirection != allowedDirection) return

        gameData.removedBlockers.add(mapTitle, rectangle)
        brokerManager.bumpObservers.removeObserver(this)
        update()
    }

    fun update() {
        if (isActive == shouldBeActive) return

        isActive = shouldBeActive
        changeBlocker()
    }

    private fun changeBlocker() {
        if (isActive) {
            brokerManager.blockObservers.addObserver(this)
        } else {
            brokerManager.blockObservers.removeObserver(this)
        }
        mapManager.setTiledGraph()
    }

    private fun createDirection(rectObject: RectangleMapObject): Direction {
        return rectObject.propertyOrNull<String>("allowedDirection")
            ?.uppercase()?.let { Direction.valueOf(it) } ?: Direction.NONE
    }

}
