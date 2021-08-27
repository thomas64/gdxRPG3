package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array


class TiledGraph(
    private val width: Int,
    private val height: Int
) : IndexedGraph<TiledNode> {

    private val nodes: Array<TiledNode> = Array(width * height)

    init {
        fillNodes()
        addConnections()
    }

    fun findPath(startPoint: Vector2, endPoint: Vector2): DefaultGraphPath<TiledNode> {
        val pathFinder = IndexedAStarPathFinder(this)
        val tiledHeuristic = TiledHeuristic()
        val path = DefaultGraphPath<TiledNode>()
        pathFinder.searchNodePath(getNode(startPoint), getNode(endPoint), tiledHeuristic, path)
        return path
    }

    private fun fillNodes() {
        (0 until width).forEach { x: Int ->
            (0 until height).forEach { y: Int ->
                nodes.add(TiledNode(x, y))
            }
        }
    }

    private fun addConnections() {
        (0 until width).forEach { x: Int ->
            val index = x * height
            (0 until height).forEach { y: Int ->
                val tiledNode = nodes[index + y]
                if (x > 0) addConnection(tiledNode, -1, 0)
                if (y > 0) addConnection(tiledNode, 0, -1)
                if (x < width - 1) addConnection(tiledNode, 1, 0)
                if (y < height - 1) addConnection(tiledNode, 0, 1)
            }
        }
    }

    private fun addConnection(tiledNode: TiledNode, xOffset: Int, yOffset: Int) {
        val target = getNode(tiledNode.x + xOffset, tiledNode.y + yOffset)
        if (!target.isCollision) {
            tiledNode.connections.add(DefaultConnection(tiledNode, target))
        }
    }

    private fun getNode(point: Vector2): TiledNode {
        return getNode(point.x.toInt(), point.y.toInt())
    }

    private fun getNode(x: Int, y: Int): TiledNode {
        return nodes[x * height + y]
    }

    override fun getIndex(tiledNode: TiledNode): Int {
        return tiledNode.getIndex(height)
    }

    override fun getNodeCount(): Int {
        return nodes.size
    }

    override fun getConnections(fromNode: TiledNode): Array<Connection<TiledNode>> {
        return fromNode.connections
    }

}
