package nl.t64.cot.components.battle

import com.fasterxml.jackson.annotation.JsonProperty


data class BattleProgress(
    val isDefeated: Boolean = false,
    val wantsToFight: Boolean = true
) {

    // a battle that nobody fought has no progress at all, so it does not have to be stored.
    fun isChanged(): Boolean {
        return this != BattleProgress()
    }
}

class Battle(
    val battlers: List<Battler> = emptyList(),
    @JsonProperty("combat_power")
    val combatPowerOverride: Float? = null,
    val background: String = "",
    val isEscapable: Boolean = true,
    var isDefeated: Boolean = false,
    var wantsToFight: Boolean = true
) {

    fun reset() {
        if (isDefeated) {
            wantsToFight = false
        }
        isDefeated = false
    }

    fun toProgress(): BattleProgress {
        return BattleProgress(isDefeated, wantsToFight)
    }

    fun applyProgress(progress: BattleProgress) {
        isDefeated = progress.isDefeated
        wantsToFight = progress.wantsToFight
    }

}
