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
        SchedulePart("honeywood", "08:52", "08:53", WEST,  WALKING, "academy_a",  "academy_b",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:53", "08:55", SOUTH, WALKING, "academy_b",  "academy_c",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:55", "08:57", EAST,  WALKING, "academy_c",  "academy_d",  "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:57", "08:58", SOUTH, WALKING, "academy_d",  "academy_e1", "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:58", "08:59", WEST,  WALKING, "academy_e1", "academy_f1", "honeywood_academy1_there"),
        SchedulePart("honeywood", "08:59", "16:59", EAST,  IDLE,    "academy_f1", "academy_f1", "honeywood_academy1"),
        SchedulePart("honeywood", "16:59", "17:00", EAST,  WALKING, "academy_f1", "academy_e1", "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:00", "17:01", NORTH, WALKING, "academy_e1", "academy_d",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:01", "17:03", WEST,  WALKING, "academy_d",  "academy_c",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:03", "17:05", NORTH, WALKING, "academy_c",  "academy_b",  "honeywood_academy1_back"),
        SchedulePart("honeywood", "17:05", "17:06", EAST,  WALKING, "academy_b",  "academy_a",  "honeywood_academy1_back"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
