package nl.t64.cot.screens.world.entity.events

import nl.t64.cot.screens.world.entity.EntityState


class OnDetectionEvent(
    val moveSpeed: Float,
    val state: EntityState
) : Event
