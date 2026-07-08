package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData


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
         * Verlaag dit als alle vijanden net iets te sterk worden ingeschat.
         * Verhoog dit als alle vijanden net iets te zwak worden ingeschat.
         */
        private const val CALIBRATION = 0.8f

        fun forBattle(battleId: String): ThreatLevel {
            val partyPower: Float = gameData.party.getAllHeroes().map { it.getCombatPower() }.sum().coerceAtLeast(1f)
            val enemyPower: Float = EnemyContainer(battleId).getTotalCombatPower()
            return fromRatio(enemyPower / partyPower * CALIBRATION)
        }

        // Grenzen geijkt op de werkelijke combat-power-spreiding van de vijanden (slime ~132 t/m orc general ~4064).
        // EVEN ligt rond ratio 1.0 (eerlijk gevecht); DANGEROUS begint bij 1.8 (boss-territorium) en
        // DEADLY pas vanaf 2.3, gereserveerd voor een echt overweldigende overmacht waar je van weg wilt blijven.
        private fun fromRatio(ratio: Float): ThreatLevel {
            return when {
                ratio < 0.3f -> TRIVIAL
                ratio < 0.8f -> WEAKER
                ratio < 1.3f -> EVEN
                ratio < 1.8f -> STRONGER
                ratio < 2.3f -> DANGEROUS
                else -> DEADLY
            }
        }
    }
}
