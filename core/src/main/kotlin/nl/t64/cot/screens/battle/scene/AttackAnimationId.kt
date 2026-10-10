package nl.t64.cot.screens.battle.scene


enum class AttackAnimationId {
    SWING,
    THRUST,
    CLAW,
    HIT,
    FIRE,
    WIND,
    THUNDER;

    val key: String = name.lowercase()
}
