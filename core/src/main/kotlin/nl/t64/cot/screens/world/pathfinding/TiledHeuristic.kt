package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.ai.pfa.Heuristic
import kotlin.math.abs


internal class TiledHeuristic : Heuristic<TiledNode> {

    override fun estimate(node: TiledNode, endNode: TiledNode): Float {
        return abs(endNode.x - node.x).toFloat() + abs(endNode.y - node.y).toFloat()
    }

}
