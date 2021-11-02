package nl.t64.cot.components.party.skills

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val SKILL_CONFIGS = "configs/skills/"
private const val FILE_LIST = SKILL_CONFIGS + "_files.txt"

object SkillDatabase {

    private val skillItems: Map<String, SkillItem> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .map { Gdx.files.internal(SKILL_CONFIGS + it).readString() }
        .map { Utils.readValue<SkillItem>(it) }
        .flatMap { it.toList() }
        .toMap()
        .onEach {
            it.value.id = SkillItemId.valueOf(it.key.uppercase())
            it.value.name = it.key.replaceFirstChar(Char::uppercase)
        }

    fun createSkillItem(skillId: String, rank: Int): SkillItem {
        val skillItem = skillItems[skillId.lowercase()]!!
        return skillItem.createCopy(rank)
    }

}
