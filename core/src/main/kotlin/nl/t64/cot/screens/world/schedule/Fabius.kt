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
            State("honeywood",      EAST,   WALKING,    7, 41,    7, 45,    550, 350,    600, 350),
            State("honeywood",      NORTH,  WALKING,    7, 45,    7, 50,    600, 350,    650, 500),
            State("honeywood_inn",  NORTH,  WALKING,    7, 50,    7, 55,    600,  50,    600, 250),
            // @formatter:on
        )
    }

}