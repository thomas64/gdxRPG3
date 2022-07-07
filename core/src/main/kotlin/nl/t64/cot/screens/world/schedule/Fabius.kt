package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsNpc
import nl.t64.cot.screens.world.entity.InputScheduledNpc
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc
import nl.t64.cot.screens.world.schedule.ScheduleState as State


class Fabius : Schedule() {

    override val entity = Entity("fabius", InputScheduledNpc(), PhysicsScheduledNpc(), GraphicsNpc("man01"))

    override fun getSchedule(): List<State> {
        return listOf(
            // @formatter:off
            State("honeywood",      SOUTH,  IDLE,       7, 30,    7, 40,    550, 350,    550, 350),
            State("honeywood",      EAST,   IDLE,       7, 40,    7, 41,    550, 350,    550, 350),
            State("honeywood",      EAST,   WALKING,    7, 41,    7, 42,    550, 350,    700, 350),
            State("honeywood",      WEST,   WALKING,    7, 42,    7, 43,    700, 350,    600, 350),
            State("honeywood",      NORTH,  WALKING,    7, 43,    7, 50,    600, 350,    624, 484),
            State("honeywood_inn",  NORTH,  WALKING,    7, 50,    7, 53,    620,  40,    620, 250),
            State("honeywood_inn",  NORTH,  IDLE,       7, 53,    9, 55,    620, 250,    620, 250),
            // @formatter:on
        )
    }

}
