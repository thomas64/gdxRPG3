package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class LastdennEntranceGuard : EntitySchedule() {

    override val entity = Entity("soldier12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("soldier12"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "07:30", "15:00", SOUTH, IDLE,     "entrance_guard1", "entrance_guard1", "guarding_till_1500",      listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "15:00", "15:15", EAST,  WALKING,  "entrance_guard1", "entrance_guard2", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "15:15", "15:20", NORTH, WALKING,  "entrance_guard2", "entrance_guard3", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "15:20", "15:30", EAST,  WALKING,  "entrance_guard3", "entrance_guard4", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "15:30", "16:30", NORTH, WALKING,  "entrance_guard4", "entrance_guard5", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "16:30", "16:40", NORTH, IDLE,     "entrance_guard5", "entrance_guard5", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "16:40", "16:50", EAST,  IDLE,     "entrance_guard5", "entrance_guard5", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "16:50", "17:00", WEST,  IDLE,     "entrance_guard5", "entrance_guard5", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "17:00", "17:59", SOUTH, WALKING,  "entrance_guard5", "entrance_guard6", "walking_to_warp_crystal", listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "17:59", "18:00", EAST,  WALKING,  "entrance_guard6", "entrance_guard7", "",                        listOf("is_lastdenn_guard_alive")),
        SchedulePart("lastdenn", "18:00", "20:00", WEST,  IMMOBILE, "entrance_guard7", "entrance_guard7", "talking_to_other_guard",  listOf("is_lastdenn_guard_alive")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
