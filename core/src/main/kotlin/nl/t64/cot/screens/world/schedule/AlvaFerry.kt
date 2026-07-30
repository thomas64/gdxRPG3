package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.EAST
import nl.t64.cot.screens.world.entity.Direction.WEST
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IMMOBILE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class AlvaFerry : EntitySchedule() {

    override val entity = Entity("ship", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("ship"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("alva_road_west", "13:00", "14:00", EAST, WALKING,  "alva_ferry1", "alva_ferry2", conditions = listOf("are_ferry_bandits_alive")),
        SchedulePart("alva_road_west", "14:00", "15:00", EAST, IMMOBILE, "alva_ferry2", "alva_ferry2", conditions = listOf("are_ferry_bandits_alive")),
        SchedulePart("alva_road_west", "15:00", "16:00", WEST, WALKING,  "alva_ferry2", "alva_ferry1", conditions = listOf("are_ferry_bandits_alive")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // todo: when the map across the water exists, carry the player along at 15:00.
        // the fare is paid when "_conv_alva_ferry_bandits_==_100" is true,
        // and mapManager.schedulePortal() is the way the map schedules already move the player.
    }

}
