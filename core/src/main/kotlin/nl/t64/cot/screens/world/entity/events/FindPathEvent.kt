package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Entity


class FindPathEvent(val playerGridPosition: Vector2, val partyMembers: List<Entity>? = null) : Event
