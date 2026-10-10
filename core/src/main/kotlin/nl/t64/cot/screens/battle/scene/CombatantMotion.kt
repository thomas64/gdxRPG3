package nl.t64.cot.screens.battle.scene


private const val MOTIONS_PER_COLUMN = 6

enum class CombatantMotion(val isLooping: Boolean) {

    WALK(true),         // frames 1-3 of row 1
    WAIT(true),         // frames 1-3 of row 2
    CHANT(true),        // frames 1-3 of row 3
    GUARD(true),        // frames 1-3 of row 4
    DAMAGE(false),      // frames 1-3 of row 5
    EVADE(false),       // frames 1-3 of row 6
    THRUST(false),      // frames 4-6 of row 1
    SWING(false),       // frames 4-6 of row 2
    RANGE(false),       // frames 4-6 of row 3
    SKILL(false),       // frames 4-6 of row 4
    SPELL(false),       // frames 4-6 of row 5
    ITEM(false),        // frames 4-6 of row 6
    ESCAPE(true),       // frames 7-9 of row 1
    VICTORY(true),      // frames 7-9 of row 2
    DYING(true),        // frames 7-9 of row 3
    ABNORMAL(true),     // frames 7-9 of row 4
    SLEEP(true),        // frames 7-9 of row 5
    DEAD(true);         // frames 7-9 of row 6

    val row: Int = ordinal % MOTIONS_PER_COLUMN
    val column: Int = ordinal / MOTIONS_PER_COLUMN

}
