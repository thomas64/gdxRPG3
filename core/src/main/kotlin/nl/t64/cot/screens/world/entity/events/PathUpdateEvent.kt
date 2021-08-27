package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import nl.t64.cot.screens.world.pathfinding.TiledNode


class PathUpdateEvent(val path: DefaultGraphPath<TiledNode>) : Event
