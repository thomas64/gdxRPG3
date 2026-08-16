package nl.t64.cot.screens.world.schedule


object DoorScheduleDatabase {

    private val doorScheduleParts: List<DoorSchedulePart> = listOf(
        // @formatter:off

        // black smith
        DoorSchedulePart("honeywood", "07:34", "07:36", "door_honeywood_smith"),
        DoorSchedulePart("honeywood", "07:58", "08:00", "door_honeywood_elder"),
        DoorSchedulePart("honeywood", "10:43", "10:45", "door_honeywood_elder"),
        DoorSchedulePart("honeywood", "10:56", "10:58", "door_honeywood_smith"),

        // deryk
        DoorSchedulePart("honeywood", "18:00", "18:02", "door_honeywood_elder"),
        DoorSchedulePart("honeywood", "18:28", "18:30", "door_honeywood_elder"),
        DoorSchedulePart("honeywood", "18:45", "18:47", "door_honeywood_inn"),

        // garrin
        DoorSchedulePart("lastdenn",  "11:23", "11:25", "door_lastdenn_garrin",         listOf("is_garrin_possessed")),
        DoorSchedulePart("lastdenn",  "14:56", "15:00", "door_lastdenn_jail",           listOf("is_garrin_possessed")),

        // santino
        DoorSchedulePart("lastdenn",  "12:29", "12:31", "door_large_round"),
        DoorSchedulePart("lastdenn",  "13:49", "13:51", "door_wooden_ring_shade_left",  listOf("!is_garrin_possessed")),
        DoorSchedulePart("lastdenn",  "15:03", "15:05", "door_wooden_ring_shade_left",  listOf("!is_garrin_possessed")),
        DoorSchedulePart("lastdenn",  "16:24", "16:26", "door_large_round",             listOf("!is_garrin_possessed")),

        // @formatter:on
    )

    fun shouldBeOpen(doorId: String): Boolean {
        return doorScheduleParts.any { it.isCurrentlyActiveFor(doorId) }
    }

}
