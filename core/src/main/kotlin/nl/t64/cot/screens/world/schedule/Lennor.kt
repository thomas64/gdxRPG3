package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Lennor : EntitySchedule() {

    override val entity = Entity("man13", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man13"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "07:34", "07:39", NORTH, RUNNING,  "lennor1", "lennor2", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:39", "07:49", NORTH, IMMOBILE, "lennor2", "lennor2", "black_and_lennor_meet"),
        SchedulePart("honeywood", "07:49", "07:51", EAST,  RUNNING,  "lennor2", "lennor3", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:51", "07:56", EAST,  RUNNING,  "lennor3", "lennor4", "lennor_on_the_way"),
        SchedulePart("honeywood", "07:56", "08:06", SOUTH, IMMOBILE, "lennor4", "lennor4", "lennor_and_johanna_meet"),
        SchedulePart("honeywood", "08:06", "08:09", EAST,  WALKING,  "lennor4", "lennor5", "lennor_on_the_way"),
        SchedulePart("honeywood", "08:09", "16:55", EAST,  IDLE,     "lennor5", "lennor5", "quest_helping_horse", listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "16:55", "17:01", EAST,  IMMOBILE, "lennor5", "lennor5", "lennor_is_finished",  listOf("_c_!=_f_q_quest_helping_horse")),
        SchedulePart("honeywood", "17:01", "20:00", NORTH, IDLE,     "lennor6", "lennor6", "the_road_is_open",    listOf("_c_!=_f_q_quest_helping_horse")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
