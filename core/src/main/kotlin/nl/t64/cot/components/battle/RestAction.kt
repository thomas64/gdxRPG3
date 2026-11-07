package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.components.party.HeroItem
import kotlin.math.roundToInt


class RestAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem
    private val apCost: Int = currentParticipant.currentAP
    private val amount: Int = calculateAmount().coerceAtMost(hero.maximumHp - hero.currentHp)
    private val message = """
        Resting will make ${character.name} end
        this turn and recover $amount HP.

        """

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < 1) {
            Pair(false, (message + "Not enough AP!").trimIndent())
        } else if (character.currentHp == character.maximumHp) {
            Pair(false, (message + "Already at maximum HP.").trimIndent())
        } else {
            Pair(true, (message + "Do you want to rest? ($apCost AP)").trimIndent())
        }
    }

    fun handle(): StatChange {
        currentParticipant.currentAP = 0
        hero.recoverPartHp(amount)
        return StatChange("+$amount", "Hit Points", Color.GREEN)
    }

    private fun calculateAmount(): Int {
        val percent: Float = apCost * 0.02f
        return (hero.maximumHp * percent).roundToInt().coerceAtLeast(1)
    }

}
