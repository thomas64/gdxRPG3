package nl.t64.cot.components.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import nl.t64.cot.resources.ConfigDataLoader
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt


private const val ROW_FORMAT: String = "%-24s %5s %4s %8s %6s"

private val combatPowerCalculator = CombatPowerCalculator()

/**
 * Not a test, but a dump tool. Nothing is asserted, it only prints.
 *
 * The combat power of an enemy is stored nowhere: [CombatPowerCalculator] calculates it from the stats, the
 * skills, the abilities and the equipment, and [XpCalculator] turns it into the xp reward. This prints the
 * whole roster in one go, so the xp tap can be tuned against real numbers instead of a gut feeling.
 *
 * Run it again after changing stats, weapons or abilities. The numbers move along.
 */
internal class EnemyPowerDump {

    @Test
    fun dumpEnemyPower() {
        Gdx.files = HeadlessFiles()
        val enemies: List<EnemyItem> = ConfigDataLoader.createEnemies().values
            .sortedBy { combatPowerCalculator.calculate(it) }

        println(ROW_FORMAT.format("enemy", "hp", "ap", "power", "xp"))
        enemies.forEach { println(it.toDumpRow()) }
    }

    private fun EnemyItem.toDumpRow(): String {
        val combatPower: Int = combatPowerCalculator.calculate(this).roundToInt()
        return ROW_FORMAT.format(id, maximumHp, getCalculatedActionPoints(), combatPower, xp)
    }

}
