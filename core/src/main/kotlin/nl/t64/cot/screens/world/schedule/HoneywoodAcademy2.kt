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
        SchedulePart("honeywood", "08:23", "08:24", WEST,  WALKING, "academy_a",  "academy_b",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:24", "08:26", SOUTH, WALKING, "academy_b",  "academy_c",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:26", "08:28", EAST,  WALKING, "academy_c",  "academy_d",  "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:28", "08:30", EAST,  WALKING, "academy_d",  "academy_e2", "honeywood_academy2_there"),
        SchedulePart("honeywood", "08:30", "17:32", NORTH, IDLE,    "academy_e2", "academy_e2", "honeywood_academy2"),
        SchedulePart("honeywood", "17:32", "17:35", WEST,  WALKING, "academy_e2", "academy_d",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:35", "17:37", WEST,  WALKING, "academy_d",  "academy_c",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:37", "17:39", NORTH, WALKING, "academy_c",  "academy_b",  "honeywood_academy2_back"),
        SchedulePart("honeywood", "17:39", "17:40", EAST,  WALKING, "academy_b",  "academy_a",  "honeywood_academy2_back"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
