package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.utils.Array
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.EntityState


private const val MAX_CONNECTIONS = 4

class TiledNode(val x: Int, val y: Int) {

    // todo, sending FLYING as an argument here can only work for flying enemies on the map.
    // walking enemies will glitch out for wanting to walk over water but not be able to.
    // this is a problem here, because with this setup, low blockers will always allow a path.
    // there must come a solution for this.
    val isCollision: Boolean = brokerManager.blockObservers.isBlockerBlockingGridPoint(x.toFloat(), y.toFloat(), EntityState.FLYING)
    val connections: Array<Connection<TiledNode>> = Array(MAX_CONNECTIONS)

    fun getIndex(mapHeight: Int): Int {
        return x * mapHeight + y
    }

}
