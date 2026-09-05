package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import kotlin.math.roundToInt


abstract class PerformingAbilityItem(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        throw IllegalStateException("PerformingAbilityItem does not support preview copies.")
    }

    override fun handleSuccess(attackData: AttackData) {
        throw IllegalStateException("PerformingAbilityItem does not support handleSuccess.")
    }

}

fun calculatePerformBonus(baseHit: Int, troubadourRank: Int): Int {
    val rankRatio: Float = (troubadourRank + 1) / 11f
    return (0.35f * (100 - baseHit) * rankRatio).roundToInt().coerceAtLeast(0)
}

fun calculatePerformPenalty(baseHit: Int, troubadourRank: Int): Int {
    val rankRatio: Float = (troubadourRank + 1) / 11f
    return (0.2f * baseHit * rankRatio).roundToInt()
}

/*
Only one participant can be performing.

Beauty      rank 1   rank 2   rank 4   rank 6   rank 8   rank 10   rank 13   rank 16
    hit 20      +5       +8      +13      +18      +23       +28       +36       +43
    hit 50      +3       +5       +8      +11      +14       +17       +22       +27
    hit 80      +1       +2       +3       +4       +6        +7        +9       +11

Chaos       rank 1   rank 2   rank 4   rank 6   rank 8   rank 10   rank 13   rank 16
    hit 20      -1       -1       -2       -3       -3        -4        -5        -6
    hit 50      -2       -3       -5       -6       -8       -10       -13       -15
    hit 80      -3       -4       -7      -10      -13       -16       -20       -25
*/
