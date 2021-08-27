package nl.t64.cot.components.battle

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val BATTLES_CONFIGS = "configs/battles/"
private const val FILE_LIST = BATTLES_CONFIGS + "_files.txt"

class BattleContainer {

    private val battles: Map<String, Battle> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .filter { it.isNotBlank() }
        .map { Gdx.files.internal(BATTLES_CONFIGS + it).readString() }
        .map { Utils.readValue<Battle>(it) }
        .flatMap { it.toList() }
        .toMap()

    fun getBattlers(battleId: String): List<Battler> = battles[battleId]!!.battlers
    fun isBattleEscapable(battleId: String): Boolean = battles[battleId]!!.isEscapable
    fun isBattleWon(battleId: String): Boolean = battles[battleId]?.hasWon ?: false
    fun setBattleWon(battleId: String) {
        battles[battleId]!!.hasWon = true
    }

}
