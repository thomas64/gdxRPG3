package nl.t64.cot.components.battle

import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellContainer
import nl.t64.cot.components.party.stats.StatContainer
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random


class EnemyItem(
    id: String = "",
    name: String = "",
    school: SchoolType = SchoolType.NONE,
    private val hp: Int = 0,
    stats: StatContainer = StatContainer(),
    skills: SkillContainer = SkillContainer(),
    spells: SpellContainer = SpellContainer(),
    inventory: EquipContainer = EquipContainer(),
    isAlive: Boolean = true,
    val xp: Int = 0,
    private val drops: Map<String, Int> = emptyMap()
) : Character(
    id, name, school, stats, skills, spells, inventory, isAlive
) {
    override val maximumHp: Int get() = if (stats.getById(StatItemId.CONSTITUTION).rank == 0) hp else stats.maximumHp

    init {
        currentHp = maximumHp
        currentSp = maximumSp
    }

    fun createCopy(
        id: String = this.id,
        name: String = this.name,
        school: SchoolType = this.school,
        hp: Int = this.hp,
        stats: StatContainer = this.stats,
        skills: SkillContainer = this.skills,
        spells: SpellContainer = this.spells,
        inventory: EquipContainer = this.inventory,
        isAlive: Boolean = this.isAlive,
        xp: Int = this.xp,
        drops: Map<String, Int> = this.drops
    ): EnemyItem {
        return EnemyItem(id, name, school, hp, stats, skills, spells, inventory, isAlive, xp, drops)
    }

    fun addDropsTo(spoils: MutableMap<String, Int>) {
        drops.forEach { it.addPossibleDropTo(spoils) }
    }

    private fun Map.Entry<String, Int>.addPossibleDropTo(spoils: MutableMap<String, Int>) {
        if (value >= Random.nextInt(0, 100)) {
            addDropTo(spoils)
        }
    }

    private fun Map.Entry<String, Int>.addDropTo(spoils: MutableMap<String, Int>) {
        spoils[key] = (spoils[key] ?: 0) + getDropAmount()
    }

    private fun Map.Entry<String, Int>.getDropAmount(): Int {
        return if (key == "gold") Random.nextInt(1, getMaxGoldLoot()) else 1
    }

    private fun getMaxGoldLoot(): Int {
        return max((xp / 2f), 1f).roundToInt()
    }

}
