package nl.t64.cot.components.battle

import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellContainer
import nl.t64.cot.components.party.stats.StatContainer


class EnemyItem(
    val name: String = "",
    private val school: SchoolType = SchoolType.NONE,
    private val stats: StatContainer = StatContainer(),
    private val skills: SkillContainer = SkillContainer(),
    private val spells: SpellContainer = SpellContainer(),
    private val inventory: EquipContainer = EquipContainer(),
    val xp: Int = 0,
    val drops: Map<String, Int> = emptyMap()
) {

    lateinit var id: String

    fun createCopy(): EnemyItem {
        val enemyCopy = EnemyItem(name, school, stats, skills, spells, inventory, xp, drops)
        enemyCopy.id = id
        return enemyCopy
    }

    fun getLevel(): Int = stats.getLevel()

}
