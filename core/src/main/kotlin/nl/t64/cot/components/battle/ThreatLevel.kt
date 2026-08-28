package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData
import kotlin.math.pow


private val combatPowerCalculator = CombatPowerCalculator()

/**
 * Een inschatting van hoe zwaar een battle is voor de huidige party.
 *
 * De maatstaf is de gevechtskracht van beide kanten, zie [CombatPowerCalculator].
 * De drempels hieronder zijn smaak en mogen vrij getuned worden.
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
         * Bijstelknop voor de hele indicator. < 1 laat vijanden zwakker tonen, > 1 sterker.
         * Verlaag dit als alle vijanden te sterk worden ingeschat.
         * Verhoog dit als alle vijanden te zwak worden ingeschat.
         */
        private const val CALIBRATION = 1f
        private const val CROWD_DAMPENER = 0.9f // < 1: meer vijanden tellen minder mee, > 1: meer vijanden tellen zwaarder mee

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

        private fun fromRatio(ratio: Float): ThreatLevel {
            return when {
                ratio < 0.1f -> TRIVIAL
                ratio < 0.7f -> WEAKER
                ratio < 1.3f -> EVEN
                ratio < 1.9f -> STRONGER
                ratio < 2.5f -> DANGEROUS
                else -> DEADLY
            }
        }
    }
}
