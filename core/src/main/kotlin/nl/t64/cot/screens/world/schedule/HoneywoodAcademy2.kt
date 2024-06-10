package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class HoneywoodAcademy2 : EntitySchedule() {

    override val entity = Entity("man15", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man15"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "08:53", "08:54", WEST,  WALKING, "academy_a",  "academy_b",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:54", "08:56", SOUTH, WALKING, "academy_b",  "academy_c",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:56", "08:58", EAST,  WALKING, "academy_c",  "academy_d",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:58", "09:00", EAST,  WALKING, "academy_d",  "academy_e2", "honeywood_academy2_there"),
        SchedulePart("honeywood", "09:00", "17:02", NORTH, IDLE,    "academy_e2", "academy_e2", "honeywood_academy2"),
        SchedulePart("honeywood", "17:02", "17:05", WEST,  WALKING, "academy_e2", "academy_d",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:05", "17:07", WEST,  WALKING, "academy_d",  "academy_c",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:07", "17:09", NORTH, WALKING, "academy_c",  "academy_b",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:09", "17:10", EAST,  WALKING, "academy_b",  "academy_a",  "honeywood_academy2_back"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
