package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData
import kotlin.math.pow


private val combatPowerCalculator = CombatPowerCalculator()

/**
 * An estimate of how hard a battle is for the current party.
 *
 * The measure is the combat power of both sides, see [CombatPowerCalculator].
 * The constants below are a matter of taste and can be tuned freely.
 */
enum class ThreatLevel(val color: Color) {
    TRIVIAL(Color.SKY),
    WEAKER(Color.GREEN),
    EVEN(Color.YELLOW),
    STRONGER(Color.ORANGE),
    DANGEROUS(Color.SCARLET),
    DEADLY(Color.RED);

    companion object {
        /**
         * Tuning knob for the whole indicator. < 1 shows enemies weaker, > 1 stronger.
         * Lower this if all enemies are estimated too strong.
         * Raise this if all enemies are estimated too weak.
         */
        private const val CALIBRATION = 1f
        private const val CROWD_DAMPENER = 0.9f // < 1: more enemies count less, > 1: more enemies count more

        fun forBattle(battleId: String): ThreatLevel {
            val partyPower: List<Float> =
                gameData.party.getAllHeroesAlive().map { combatPowerCalculator.calculate(it) }
            val dampenedPartyPower: Float = dampenedPowerOf(partyPower)

            val dampenedEnemyPower: Float = gameData.battles.getCombatPowerOverride(battleId)
                ?: dampenedPowerOf(EnemyContainer(battleId).getAll().map { combatPowerCalculator.calculate(it) })

            return fromRatio((dampenedEnemyPower / dampenedPartyPower) * CALIBRATION)
        }

        private fun dampenedPowerOf(powers: List<Float>): Float {
            return powers.sum() * powers.size.toFloat().pow(CROWD_DAMPENER - 1f)
        }

        /**
         * A ratio of 2 means the enemies are 2x stronger than the party.
         * A ratio of 0.5 means the party is 2x stronger than the enemies (1 / 2 = 0.5).
         * So the lower thresholds are mirrors of the upper ones: 1 / 1.2 = 0.83, 1 / 2.49 = 0.4.
         * Each step up is x1.44: 1.2 * 1.44 = 1.73, 1.73 * 1.44 = 2.49.
         */
        private fun fromRatio(ratio: Float): ThreatLevel {
            return when {
                ratio < 0.4f -> TRIVIAL         // party is more than 2.49x stronger
                ratio < 0.83f -> WEAKER         // party is 1.2x to 2.49x stronger
                ratio < 1.2f -> EVEN            // either side is at most 1.2x stronger
                ratio < 1.73f -> STRONGER       // enemies are 1.2x to 1.73x stronger
                ratio < 2.49f -> DANGEROUS      // enemies are 1.73x to 2.49x stronger
                else -> DEADLY                  // enemies are more than 2.49x stronger
            }
        }
    }
}
