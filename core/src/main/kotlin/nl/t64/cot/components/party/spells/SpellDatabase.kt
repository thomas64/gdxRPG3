package nl.t64.cot.components.party.spells

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val SPELL_CONFIGS = "configs/spells/"
private const val FILE_LIST = SPELL_CONFIGS + "_files.txt"

object SpellDatabase {

    private val spellItems: Map<String, SpellItem> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .map { Gdx.files.internal(SPELL_CONFIGS + it).readString() }
        .map { Utils.readValue<SpellItem>(it) }
        .flatMap { it.toList() }
        .toMap()
        .onEach { it.value.id = it.key }


    fun createSpellItem(spellId: String, rank: Int): SpellItem {
        val spellItem = spellItems[spellId]!!
        return spellItem.createCopy(rank)
    }

}
