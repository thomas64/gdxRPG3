package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.WEST
import nl.t64.cot.screens.world.entity.EntityState.IMMOBILE


class GarrinDead : EntitySchedule() {

    override val entity = Entity("man12_dead", InputEmpty(), PhysicsScheduledNpc().apply { boundingBoxHeightPercentage = 0.85f }, GraphicsScheduledNpc("man12_dead"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "09:54", "10:05", WEST,  IMMOBILE,  "garrin7",  "garrin7"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
