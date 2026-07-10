package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData
import kotlin.math.pow


/**
 * Een inschatting van hoe zwaar een battle is voor de huidige party.
 *
 * De maatstaf is de afgeleide gevechtskracht ([Character.getCombatPower]): overlevingsvermogen × offense.
 * Bewust NIET de bestede xp, want "dump stats" die een vijand niet gebruikt (bv. een lage intelligence bij
 * een melee-vechter) drukken de build-xp onterecht omlaag. Combat power kijkt alleen naar wat het gevecht
 * echt bepaalt (hp, bescherming, damage, hit, actiepunten) en negeert zulke ongebruikte stats vanzelf.
 * Beide kanten worden op identieke wijze berekend, zodat het een eerlijke vergelijking is.
 * De drempels hieronder zijn smaak en mogen vrij getuned worden.
 */
enum class ThreatLevel(val color: Color) {
    TRIVIAL(Color.SKY),
    WEAKER(Color.GREEN),
    EVEN(Color.YELLOW),
    STRONGER(Color.ORANGE),
    DANGEROUS(Color.RED),
    DEADLY(Color.RED);

    companion object {
        /**
         * Bijstelknop voor de hele indicator. < 1 laat vijanden zwakker tonen, > 1 sterker.
         * Verlaag dit als alle vijanden te sterk worden ingeschat.
         * Verhoog dit als alle vijanden te zwak worden ingeschat.
         */
        private const val CALIBRATION = 1.0f
        private const val CROWD_DAMPENER = 0.8f // < 1: meer vijanden tellen minder mee, > 1: meer vijanden tellen zwaarder mee

        fun forBattle(battleId: String): ThreatLevel {
            val partyPower: List<Float> = gameData.party.getAllHeroesAlive().map { it.getCombatPower() }
            val enemyPower: List<Float> = EnemyContainer(battleId).getAll().map { it.getCombatPower() }
            val dampenedPartyPower: Float = dampenedPowerOf(partyPower)
            val dampenedEnemyPower: Float = dampenedPowerOf(enemyPower)
            return fromRatio(dampenedEnemyPower / dampenedPartyPower * CALIBRATION)
        }

        private fun dampenedPowerOf(powers: List<Float>): Float {
            return powers.sum() * powers.size.toFloat().pow(CROWD_DAMPENER - 1f)
        }

        // Grenzen geijkt op de werkelijke combat-power-spreiding van de vijanden (slime ~132 t/m orc general ~4064).
        // EVEN ligt rond ratio 1.0 (eerlijk gevecht); DANGEROUS begint bij 1.9 (boss-territorium) en
        // DEADLY pas vanaf 2.5, gereserveerd voor een echt overweldigende overmacht waar je van weg wilt blijven.
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
