package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.*
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Lennor : EntitySchedule() {

    override val entity = Entity("man13", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man13"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "07:32", "07:37", NORTH, RUNNING,  "lennor1", "lennor2", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:37", "07:47", NORTH, IMMOBILE, "lennor2", "lennor2", "black_and_lennor_meet"),
        SchedulePart("honeywood", "07:47", "07:49", EAST,  RUNNING,  "lennor2", "lennor3", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:49", "07:54", EAST,  RUNNING,  "lennor3", "lennor4", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:54", "08:04", SOUTH, IMMOBILE, "lennor4", "lennor4", "lennor_and_johanna_meet"),
        SchedulePart("honeywood", "08:04", "08:07", EAST,  WALKING,  "lennor4", "lennor5", "lennor_on_the_way"),
        SchedulePart("honeywood", "08:07", "12:55", EAST,  IDLE,     "lennor5", "lennor5", "quest_helping_horse", listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "12:55", "13:01", EAST,  IMMOBILE, "lennor5", "lennor5", "lennor_is_finished",  listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "13:01", "19:30", NORTH, IDLE,     "lennor6", "lennor6", "the_road_is_open",    listOf("_c_!=_f_q_quest_helping_horse")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
