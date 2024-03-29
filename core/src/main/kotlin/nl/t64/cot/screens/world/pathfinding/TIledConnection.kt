package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.ai.pfa.Connection


class TiledConnection(
    private val fromNode: TiledNode,
    private val toNode: TiledNode,
    private val cost: Float
) : Connection<TiledNode> {

    override fun getCost(): Float {
        return cost
    }

    override fun getFromNode(): TiledNode {
        return fromNode
    }

    override fun getToNode(): TiledNode {
        return toNode
    }

}
