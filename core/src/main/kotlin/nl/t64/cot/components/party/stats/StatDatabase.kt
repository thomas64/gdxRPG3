package nl.t64.cot.components.party.stats

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val STAT_CONFIGS = "configs/stats/stats.json"

object StatDatabase {

    private val statItems: Map<String, StatItem> = Utils.readValue<StatItem>(
        Gdx.files.internal(STAT_CONFIGS).readString())
        .onEach {
            it.value.id = StatItemId.valueOf(it.key.uppercase())
            it.value.name = it.key.replaceFirstChar(Char::uppercase)
        }

    fun createStatItem(statId: String, rank: Int): StatItem {
        val statItem = statItems[statId]!!
        return statItem.createCopy(rank)
    }

}
