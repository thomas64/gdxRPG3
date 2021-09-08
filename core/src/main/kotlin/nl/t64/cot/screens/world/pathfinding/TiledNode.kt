package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.EntityState


private const val MAX_CONNECTIONS = 4

class TiledNode(val x: Int, val y: Int) {

    val connections: Array<Connection<TiledNode>> = Array(MAX_CONNECTIONS)

    fun isCollision(state: EntityState): Boolean {
        return brokerManager.blockObservers.isBlockerBlockingGridPoint(x.toFloat(), y.toFloat(), state)
    }

    fun getIndex(mapHeight: Int): Int {
        return x * mapHeight + y
    }

}
