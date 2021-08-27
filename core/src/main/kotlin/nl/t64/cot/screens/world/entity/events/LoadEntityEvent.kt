package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class LoadEntityEvent : Event {

    val state: EntityState?
    val direction: Direction?
    val position: Vector2
    val conversationOrBattleId: String?

    constructor(position: Vector2) {
        this.state = null
        this.direction = null
        this.position = position
        this.conversationOrBattleId = null
    }

    constructor(direction: Direction, position: Vector2) {
        this.state = null
        this.direction = direction
        this.position = position
        this.conversationOrBattleId = null
    }

    constructor(state: EntityState, position: Vector2) {
        this.state = state
        this.direction = null
        this.position = position
        this.conversationOrBattleId = null
    }

    constructor(state: EntityState, direction: Direction, position: Vector2) {
        this.state = state
        this.direction = direction
        this.position = position
        this.conversationOrBattleId = null
    }

    constructor(state: EntityState, direction: Direction, position: Vector2, conversationOrBattleId: String) {
        this.state = state
        this.direction = direction
        this.position = position
        this.conversationOrBattleId = conversationOrBattleId
    }

}
