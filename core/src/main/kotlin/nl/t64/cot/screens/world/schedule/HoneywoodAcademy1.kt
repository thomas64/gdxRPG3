package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class HoneywoodAcademy1 : EntitySchedule() {

    override val entity = Entity("man11", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man11"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "08:22", "08:23", WEST,  WALKING, "academy_a",  "academy_b",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:23", "08:25", SOUTH, WALKING, "academy_b",  "academy_c",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:25", "08:27", EAST,  WALKING, "academy_c",  "academy_d",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:27", "08:28", SOUTH, WALKING, "academy_d",  "academy_e1", "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:28", "08:29", WEST,  WALKING, "academy_e1", "academy_f1", "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:29", "17:29", EAST,  IDLE,    "academy_f1", "academy_f1", "honeywood_academy1"),
        SchedulePart("honeywood", "17:29", "17:30", EAST,  WALKING, "academy_f1", "academy_e1", "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:30", "17:31", NORTH, WALKING, "academy_e1", "academy_d",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:31", "17:33", WEST,  WALKING, "academy_d",  "academy_c",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:33", "17:35", NORTH, WALKING, "academy_c",  "academy_b",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:35", "17:36", EAST,  WALKING, "academy_b",  "academy_a",  "honeywood_academy1_back"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
