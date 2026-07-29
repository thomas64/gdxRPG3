package nl.t64.cot.components.battle

import kotlin.math.roundToInt


/**
 * Hoeveel gevechtskracht ([Character.getCombatPower]) één xp waard is.
 *
 * Geijkt op de gewone ladder, van blue_bat tot en met de orc sergeants: daar lag de verhouding
 * tussen gevechtskracht en de handmatig gekozen xp vrijwel constant, en die xp was doordacht.
 * De bosses zijn buiten de ijking gehouden omdat hun oude xp placeholders waren; boven het
 * bereik van de sergeants is er dus geen ankerpunt en is recht evenredig simpelweg de
 * eenvoudigste aanname, niet een gecontroleerde uitkomst. Krijgen de bosses ooit een
 * doordachte waarde, ijk deze constante dan opnieuw.
 *
 * Dit is de globale xp-kraan: verlaag de waarde om overal meer xp uit te delen, verhoog hem voor minder.
 */
private const val COMBAT_POWER_PER_XP: Float = 37f

/**
 * Zet de gevechtskracht van een vijand om in de xp die het verslaan ervan oplevert.
 *
 * Bewust een afgeleide en geen handmatig getal: een vijand die sterker wordt gemaakt,
 * wordt daarmee vanzelf meer xp waard.
 */
class XpCalculator {

    fun calculate(combatPower: Float): Int {
        return (combatPower / COMBAT_POWER_PER_XP).roundToInt().coerceAtLeast(1)
    }

}
