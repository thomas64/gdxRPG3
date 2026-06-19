package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.entity.StateIcon


class UpdateScheduledEntityEvent(
    val state: EntityState,
    val direction: Direction,
    val position: Vector2,
    val conversationId: String,
    val stateIcon: StateIcon?
) : Event
