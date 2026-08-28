package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.AbilityContainer
import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.stats.StatContainer
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random


private val combatPowerCalculator = CombatPowerCalculator()
private val xpCalculator = XpCalculator()

class EnemyItem(
    id: String = "",
    name: String = "",
    gender: String = "",
    private val hp: Int = 0,
    private val ap: Int = 0,
    stats: StatContainer = StatContainer(),
    skills: SkillContainer = SkillContainer(),
    abilities: AbilityContainer = AbilityContainer(),
    inventory: EquipContainer = EquipContainer(),
    val meleeWeapon: String? = null,
    isAlive: Boolean = true,
    private val drops: Map<String, Int> = emptyMap()
) : Character(
    id, name, gender, stats, skills, abilities, inventory, isAlive
) {
    override val maximumHp: Int get() = if (stats.getById(StatItemId.CONSTITUTION).rank == 0) hp else stats.maximumHp
    override val hasUnlimitedSp: Boolean = true
    val xp: Int = xpCalculator.calculate(combatPowerCalculator.calculate(this))
    @Transient
    var stashedWeapon: InventoryItem? = meleeWeapon?.let { InventoryDatabase.createInventoryItem(it) }

    init {
        currentHp = maximumHp
        currentSp = maximumSp
    }

    fun createCopy(
        id: String = this.id,
        name: String = this.name,
        gender: String = this.gender,
        hp: Int = this.hp,
        ap: Int = this.ap,
        stats: StatContainer = this.stats,
        skills: SkillContainer = this.skills,
        abilities: AbilityContainer = this.abilities,
        inventory: EquipContainer = this.inventory.createCopy(),
        meleeWeapon: String? = this.meleeWeapon,
        isAlive: Boolean = this.isAlive,
        drops: Map<String, Int> = this.drops
    ): EnemyItem {
        return EnemyItem(id, name, gender, hp, ap, stats, skills, abilities, inventory, meleeWeapon, isAlive, drops)
    }

    override fun getCalculatedActionPoints(): Int {
        return ap.takeUnless { it == 0 } ?: super.getCalculatedActionPoints()
    }

    fun swapToStashedWeapon() {
        val current: InventoryItem = getInventoryItem(InventoryGroup.WEAPON)!!
        forceSetInventoryItemFor(InventoryGroup.WEAPON, stashedWeapon!!)
        stashedWeapon = current
    }

    fun addDropsTo(spoils: MutableMap<String, Int>) {
        drops.forEach { it.addPossibleDropTo(spoils) }
    }

    private fun Map.Entry<String, Int>.addPossibleDropTo(spoils: MutableMap<String, Int>) {
        if (value > Random.nextInt(0, 100)) {
            addDropTo(spoils)
        }
    }

    private fun Map.Entry<String, Int>.addDropTo(spoils: MutableMap<String, Int>) {
        spoils[key] = (spoils[key] ?: 0) + getDropAmount()
    }

    private fun Map.Entry<String, Int>.getDropAmount(): Int {
        return if (key == "gold") createRandomAmount() else 1
    }

    private fun createRandomAmount(): Int {
        return Random.nextInt(1, getMaxGoldLoot())
    }

    private fun getMaxGoldLoot(): Int {
        return max((xp / 2f), 1f).roundToInt()
    }

}
