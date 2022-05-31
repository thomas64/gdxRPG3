package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState


class BlockSubject {

    private val observers: MutableList<BlockObserver> = ArrayList()

    fun addObserver(observer: BlockObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: BlockObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun removeAllNpcObservers() {
        observers
            .filterIsInstance<Entity>()
            .filter { it.isNpc() }
            .forEach { removeObserver(it) }
    }

    fun getCurrentBlockersFor(boundingBox: Rectangle, entityState: EntityState): List<Rectangle> {
        return ArrayList(observers).mapNotNull { it.getBlockerFor(boundingBox, entityState) }
    }

    fun isBlockerBlockingGridPoint(x: Float, y: Float, entityState: EntityState): Boolean {
        return ArrayList(observers).any { it.isBlocking(getGridPoint(x, y), entityState) }
    }

    private fun getGridPoint(x: Float, y: Float): Vector2 {
        return Vector2(x * Constant.HALF_TILE_SIZE + 1f, y * Constant.HALF_TILE_SIZE + 1f)
    }

}
