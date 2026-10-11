package nl.t64.cot.screens.battle.scene


enum class BattleSceneAnimationId {
    SWING,
    THRUST,
    CLAW,
    HIT,
    FIRE,
    WIND,
    THUNDER;

    val key: String = name.lowercase()
}
