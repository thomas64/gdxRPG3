package nl.t64.cot.components.battle

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.abilities.AbilityContainer
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatContainer
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random


private val armorReductionCalculator = ArmorReductionCalculator()

abstract class Character(
    val id: String = "",
    val name: String = "",
    val gender: String = "",
    protected val stats: StatContainer = StatContainer(),
    protected val skills: SkillContainer = SkillContainer(),
    protected val abilities: AbilityContainer = AbilityContainer(),
    protected val inventory: EquipContainer = EquipContainer(),
    var isAlive: Boolean = true
) {
    val isDead: Boolean get() = !isAlive
    open val maximumHp: Int get() = stats.maximumHp
    var currentHp: Int = 0
    val maximumSp: Int get() = stats.maximumSp
    var currentSp: Int = 0
    @Transient
    val bonus: BonusContainer = BonusContainer()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // todo, speciale bonus toepassen inventoryItem: epic_ring_of_healing, die 1 hp geeft om je leven 1malig te redden.
    fun takeDamage(damage: Int) {
        currentHp = (currentHp - damage).coerceAtLeast(0)
        if (currentHp <= 0) {
            currentSp = 0
            isAlive = false
        }
    }

    fun recoverFullHp() {
        currentHp = maximumHp
    }

    fun recoverPartHp(healPoints: Int) {
        currentHp = (currentHp + healPoints).coerceAtMost(maximumHp)
    }

    fun recoverFullSp() {
        currentSp = maximumSp
    }

    fun recoverPartSp(recoverPoints: Int) {
        currentSp = (currentSp + recoverPoints).coerceAtMost(maximumSp)
    }

    fun canUsePotion(potion: InventoryItem): Boolean {
        if (isDead) return false

        val needsHp: Boolean = potion.hp > 0 && currentHp < maximumHp
        val needsSp: Boolean = potion.sp > 0 && currentSp < maximumSp
        val canUseBuff: Boolean = bonus.wouldPotionHaveEffect(potion)

        return needsHp || needsSp || canUseBuff
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getInventoryItem(inventoryGroup: InventoryGroup): InventoryItem? {
        return inventory.getInventoryItem(inventoryGroup)
    }

    fun clearInventoryItemFor(inventoryGroup: InventoryGroup) {
        inventory.clearInventoryItem(inventoryGroup)
    }

    fun forceSetInventoryItemFor(inventoryGroup: InventoryGroup, inventoryItem: InventoryItem) {
        inventory.forceSetInventoryItem(inventoryGroup, inventoryItem)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getAllAbilities(): List<AbilityItem> {
        return abilities.getAll()
    }

    /**
     * Een ruwe inschatting van de gevechtskracht: overlevingsvermogen × offense-over-tijd.
     * Bewust gebaseerd op de afgeleide gevechtswaarden i.p.v. de bestede xp, zodat "dump stats"
     * die een vijand niet gebruikt (bv. een lage intelligence bij een melee-vechter) niet meetellen.
     *
     * De offense telt niet alleen damage per beurt, maar ook hoe vaak een character aan de beurt komt:
     * speed bepaalt de beurt-frequentie (zie Participant.updateTurnCounter), dus een snelle vijand met
     * lage stats (zoals de green imp) deelt veel meer schade uit dan z'n losse stats doen vermoeden.
     */
    fun getCombatPower(): Float {
        val isUnarmed: Boolean = inventory.getSkillOfCurrentWeapon() == null
        val effectiveHp: Float = maximumHp.coerceAtLeast(1) * getSurvivabilityMultiplier()
        val damage: Int = if (isUnarmed) {
            6       // placeholder for minimal damage
        } else {
            getCalculatedTotalDamage().coerceAtLeast(1)
        }
        val hitChance: Float = if (isUnarmed) {
            0.6f     // 60%. placeholder for minimal hit chance, both are only used for threat level calculation.
        } else {
            getCalculatedTotalHit().coerceIn(1, 100) / 100f
        }
        val actionsPerTurn: Int = getCalculatedActionPoints().coerceAtLeast(1)
        val turnFrequency: Int = 10 + getCalculatedTotalStatOf(StatItemId.SPEED)
        val offenseOverTime: Float = damage * hitChance * actionsPerTurn * turnFrequency
        return sqrt(effectiveHp * offenseOverTime)
    }

    /**
     * Hoeveel langer een character het uithoudt door bescherming. Alle drie de bronnen zijn percentages:
     * fysieke en magische bescherming zijn alternatieve damage-types, dus die middelen we; defense is een
     * blokkans die daar als aparte laag bovenop komt.
     */
    private fun getSurvivabilityMultiplier(): Float {
        val averageProtection: Float =
            (getCalculatedTotalProtection() + getCalculatedTotalMagicProtection()).coerceAtLeast(0) / 2f
        val blockChance: Int = getCalculatedTotalDefense().coerceIn(0, 100)
        return (1f + averageProtection / 100f) * (1f + blockChance / 100f)
    }

    fun getCalculatedTotalStatOf(statItemId: StatItemId): Int {
        val statItem = stats.getById(statItemId)
        return getRealTotalStatOf(statItem).takeIf { it > 0 } ?: 1
    }

    fun getCalculatedTotalSkillOf(skillItemId: SkillItemId): Int {
        val skillItem = skills.getById(skillItemId)
        if (skillItem.rank <= 0) return 0
        return getRealTotalSkillOf(skillItem).takeIf { it > 0 } ?: 0
    }

    private fun getRealTotalStatOf(statItem: StatItem): Int {
        return statItem.rank + inventory.getSumOfStat(statItem.id) + bonus.getStatBonus(statItem.id)
    }

    private fun getRealTotalSkillOf(skillItem: SkillItem): Int {
        return skillItem.rank + inventory.getSumOfSkill(skillItem.id) + bonus.getSkillBonus(skillItem.id)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun getCalculatedActionPoints(): Int {
        return ((getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)
            + getCalculatedTotalStatOf(StatItemId.DEXTERITY)
            + getCalculatedTotalStatOf(StatItemId.STRENGTH)
            + getCalculatedTotalStatOf(StatItemId.SPEED)) / 20f)
            .roundToInt() + 2
    }

    fun getCalculatedTotalHitWithBonus(): Int {
        return (getCalculatedTotalHit() + bonus.getHitBonus()).coerceAtLeast(1)
    }

    fun getCalculatedTotalHit(): Int {
        val weaponSkill: SkillItemId = inventory.getSkillOfCurrentWeapon() ?: return 0

        val weaponHit: Int = getSumOfEquipmentOfCalc(CalcAttributeId.BASE_HIT)
        val weaponSkillAmount: Int = getCalculatedTotalSkillOf(weaponSkill)
        val attackerHit: Float = (weaponHit / 100f) * (5f * weaponSkillAmount)
        return (weaponHit + attackerHit).roundToInt()
    }

    fun getCalculatedTotalDamage(): Int {
        if (inventory.getSkillOfCurrentWeapon() == null) return 0

        val currentWeaponMinimal: StatItemId = inventory.getStatItemIdOfMinimalOfCurrentWeapon()!!
        // todo, moet voor ranged wel sum of all equipment zijn? volgens mij geven andere item geen damage meer.
        val totalDamageOfAllEquipment: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val attributeAmount: Int = getCalculatedTotalStatOf(currentWeaponMinimal)
        val attackerDamage: Float = (totalDamageOfAllEquipment / 100f) * (5f * attributeAmount)
        return (totalDamageOfAllEquipment + attackerDamage).roundToInt()
    }

    fun getCalculatedTotalProtection(): Int {
        val totalRaw: Int = getRawProtection() + getRawExtraProtection()
        return getArmorReductionPercentage(totalRaw)
    }

    fun getRawProtection(): Int {
        return getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION)
    }

    fun getRawExtraProtection(): Int {
        return inventory.getBonusProtectionWhenArmorSetIsComplete() + bonus.getProtection()
    }

    private fun getArmorReductionPercentage(rawProtection: Int): Int {
        return armorReductionCalculator.toPercent(rawProtection)
    }

    fun getCalculatedTotalMagicProtection(): Int {
        return (getCalculatedMagicProtection() + getPossibleExtraMagicProtection()).coerceAtMost(90)
        // maximaal 90% protection, net zoals normale protection, zie armorReductionCalculator.
    }

    fun getCalculatedMagicProtection(): Int {
        // todo, moet nog een betere formule komen, maar dit is voorlopig een start. een curve ook?
        return getCalculatedTotalStatOf(StatItemId.WILLPOWER) * 2
    }

    fun getPossibleExtraMagicProtection(): Int {
        return 0 // todo, resistance spell? magic protection potion? wat moet hier komen?
    }

    fun getCalculatedTotalDefense(): Int {
        return when {
            inventory.getInventoryItem(InventoryGroup.SHIELD) == null -> 0
            else -> {
                val shieldDefense: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DEFENSE)
                val shieldSkillAmount: Int = getCalculatedTotalSkillOf(SkillItemId.SHIELD)
                val defenderDefense: Float = (shieldDefense / 100f) * (10f * shieldSkillAmount)
                return (shieldDefense + defenderDefense).roundToInt()
            }
        }
    }

    fun getSumOfEquipmentOfCalc(calcAttributeId: CalcAttributeId): Int {
        return inventory.getSumOfCalc(calcAttributeId)
    }

    fun applyGamblerBonusTo(amount: Float): Float {
        val gamblerRank: Int = getCalculatedTotalSkillOf(SkillItemId.GAMBLER)
        if (gamblerRank <= 0) return amount
        val maxBonus: Float = amount * (gamblerRank * 0.05f)
        val gamblerBonus: Float = Random.nextFloat() * (maxBonus * 2) - maxBonus
        return amount + gamblerBonus
    }

}
