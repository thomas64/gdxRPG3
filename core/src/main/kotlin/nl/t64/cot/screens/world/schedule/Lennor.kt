package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.*
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Lennor : EntitySchedule() {

    override val entity = Entity("man02", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man02"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "07:32", "07:35", NORTH, RUNNING,  "lennor1", "lennor2"),
        SchedulePart("honeywood", "07:35", "07:45", EAST,  IMMOBILE, "lennor2", "lennor2"),
        SchedulePart("honeywood", "07:45", "07:47", EAST,  RUNNING,  "lennor2", "lennor3"),
        SchedulePart("honeywood", "07:47", "07:52", EAST,  RUNNING,  "lennor3", "lennor4"),
        SchedulePart("honeywood", "07:52", "07:54", EAST,  RUNNING,  "lennor4", "lennor5"),
        SchedulePart("honeywood", "07:54", "12:45", EAST,  IDLE,     "lennor5", "lennor5", "quest_helping_horse", listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "12:45", "13:00", EAST,  IDLE,     "lennor5", "lennor5", "lennor_is_finished",  listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "13:00", "13:02", EAST,  IDLE,     "lennor5", "lennor5", "",                    listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "13:02", "13:04", WEST,  WALKING,  "lennor5", "lennor6", "",                    listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "13:04", "19:30", WEST,  IDLE,     "lennor6", "lennor6", "",                    listOf("_c_!=_f_q_quest_helping_horse")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
