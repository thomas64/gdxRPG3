package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.math.roundToInt
import kotlin.random.Random


private const val HIT_ADVANTAGE = 10
private const val HIT_DISADVANTAGE = -10
private const val DAMAGE_NORMAL_DIVISOR = 1.0f
private const val DAMAGE_DISADVANTAGE_DIVISOR = 1.5f
private const val CRIT_HIT_ADVANTAGE = 30
private const val CRIT_HIT_DISADVANTAGE = -30
private const val CRIT_DAMAGE_MULTIPLIER = 1.75f

abstract class BattleAbilityItem(
    val abilityItem: AbilityItem,
    val attacker: Participant
) {
    protected val id: AbilityItemId = abilityItem.id
    val name: String = abilityItem.name
    val ap: Int = calculateAp()
    val sp: Int = abilityItem.sp

    protected val currentWeapon: InventoryItem? get() = attacker.character.getInventoryItem(InventoryGroup.WEAPON)

    lateinit var target: Participant

    private fun calculateAp(): Int {
        if (abilityItem.ap == 99) {
            if (attacker.currentAP > attacker.maximumAP) {
                return attacker.currentAP
            } else {
                return attacker.maximumAP
            }
        } else {
            return abilityItem.ap
        }
    }

    override fun toString(): String {
        if (name == "Back") return name
        val apField = String.format("%3d AP", ap)
        val spField = if (sp > 0) " | $sp SP" else "       "
        val totalWidth = 29
        val leftPart = name
        val rightPart = "$apField$spField"
        val spaces = " ".repeat((totalWidth - leftPart.length - rightPart.length).coerceAtLeast(1))
        return "${possibleGetGrayPrefix()}$leftPart$spaces$rightPart"
    }

    abstract fun createCopyForPreview(): BattleAbilityItem
    abstract fun createPreviewMessage(): String
    abstract fun handleSuccess(attackData: AttackData)

    open fun handle(): List<AttackData> {
        val attackDataList = mutableListOf<AttackData>()
        handleSingleAttack(attackDataList)
        return attackDataList
    }

    protected fun handleSingleAttack(attackDataList: MutableList<AttackData>) {
        val singleAttack = AttackData()
        singleAttack.attacker = attacker.character.name
        singleAttack.target = target.character.name

        if (isHit()) {
            handleHit(singleAttack)
        } else {
            handleFailure(singleAttack)
        }

        attackDataList.add(singleAttack)
    }

    protected open fun isHit(): Boolean {
        return calculateHitPercentage() > Random.nextInt(0, 100)
    }

    private fun handleHit(attackData: AttackData) {
        if (isBlock()) {
            handleBlock(attackData)
        } else {
            handleSuccess(attackData)
        }

        handleDurability(attackData)

        if (target.character.isDead) {
            attackData.isTargetDead = true
        }
    }

    private fun isBlock(): Boolean {
        return target.character.getCalculatedTotalDefense() > Random.nextInt(0, 100)
    }

    private fun handleFailure(attackData: AttackData) {
        attackData.isMissed = true
    }

    private fun handleBlock(attackData: AttackData) {
        attackData.isBlocked = true
        val shield: InventoryItem = target.character.getInventoryItem(InventoryGroup.SHIELD)!!
        shield.durability--
        if (shield.durability <= 0) {
            attackData.targetShieldBrokeMessage = "${target.character.name} ${shield.name} broke!"
            target.character.clearInventoryItemFor(InventoryGroup.SHIELD)
        }
    }

    private fun handleDurability(attackData: AttackData) {
        val weapon = currentWeapon!!
        weapon.durability--
        if (attacker.isHero && weapon.durability <= 0) {
            attackData.attackerWeaponBrokeMessage = "Your ${weapon.name} broke!"
            attacker.character.clearInventoryItemFor(InventoryGroup.WEAPON)
        }
    }

    fun hasEnoughApSp(): Boolean {
        return attacker.currentAP >= ap
            && attacker.character.currentSp >= sp
    }

    fun hasEnoughResources(): Boolean {
        return when (abilityItem.requiredResource) {
            ResourceType.NONE -> true
            else -> gameData.inventory.hasEnoughOfItem(abilityItem.requiredResource.name.lowercase(), 1)
        }
    }

    fun isWeaponAllowed(): Boolean {
        return abilityItem.isWeaponAllowed(currentWeapon)
    }

    fun calculateHitPercentage(): Int {
        val baseHit: Float = attacker.character.getCalculatedTotalHitWithBonus() * abilityItem.hitMultiplier
        val hitWithGambler: Float = attacker.character.applyGamblerBonusTo(baseHit)
        val weaponTriangle: Int = getAdvantageBonusHit()
        return (hitWithGambler + weaponTriangle).roundToInt().coerceAtLeast(0)
    }

    protected fun calculateHitPercentageForVisual(): Int {
        val baseHit: Float = attacker.character.getCalculatedTotalHitWithBonus() * abilityItem.hitMultiplier
        val weaponTriangle: Int = getAdvantageBonusHit()
        return (baseHit + weaponTriangle).roundToInt().coerceAtLeast(0).coerceAtMost(100)
    }

    fun calculateCriticalHitPercentage(): Int {
        val attackerCriticalHitPercentage: Int = attacker.character.getCalculatedTotalSkillOf(SkillItemId.WARRIOR) * 4
        val weaponTriangle: Int = getAdvantageBonusCrit()
        return (attackerCriticalHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    protected fun calculateCriticalDamage(): Int {
        return (calculateDamage() * CRIT_DAMAGE_MULTIPLIER).roundToInt()
    }

    fun calculateDamage(): Int {
        val baseDamage: Float = attacker.character.getCalculatedTotalDamage() * abilityItem.damageMultiplier
        val damageWithGambler: Float = attacker.character.applyGamblerBonusTo(baseDamage)
        val protection: Int = target.character.getCalculatedTotalProtection()
        val modifiedDamage: Float = damageWithGambler - protection
        val weaponTriangle: Float = getAdvantageBonusDamage()
        return (modifiedDamage / weaponTriangle).roundToInt().coerceAtLeast(1)
    }

    protected fun calculateDamageForVisual(): Int {
        val baseDamage: Float = attacker.character.getCalculatedTotalDamage() * abilityItem.damageMultiplier
        val protection: Int = target.character.getCalculatedTotalProtection()
        val modifiedDamage: Float = baseDamage - protection
        val weaponTriangle: Float = getAdvantageBonusDamage()
        return (modifiedDamage / weaponTriangle).roundToInt().coerceAtLeast(1)
    }

    protected fun createNoWeaponMessage(): String {
        return """
            $name

            No weapon equipped.
            [FIREBRICK]Disadvantage![BLACK]
        """.trimIndent()
    }

    protected fun createEffectiveMessage(): String {
        return when {
            hasWeaponTriangleAdvantage() -> {
                """[BLUE]Advantage![BLACK]
                   |"""
            }
            hasWeaponTriangleDisadvantage() -> {
                """[FIREBRICK]Disadvantage![BLACK]
                   |"""
            }
            else -> {
                """[GRAY]Neither advantage
                   |nor disadvantage.[BLACK]
                   |"""
            }
        }
    }

    private fun possibleGetGrayPrefix(): String {
        if (abilityItem.isPreview) return ""
        return if (!isWeaponAllowed() || !hasEnoughApSp()) "[GRAY]" else ""
    }

    private fun getAdvantageBonusHit(): Int {
        return when {
            hasWeaponTriangleAdvantage() -> HIT_ADVANTAGE
            hasWeaponTriangleDisadvantage() -> HIT_DISADVANTAGE
            else -> 0
        }
    }

    private fun getAdvantageBonusCrit(): Int {
        return when {
            hasWeaponTriangleAdvantage() -> CRIT_HIT_ADVANTAGE
            hasWeaponTriangleDisadvantage() -> CRIT_HIT_DISADVANTAGE
            else -> 0
        }
    }

    private fun getAdvantageBonusDamage(): Float {
        return if (hasWeaponTriangleDisadvantage()) {
            DAMAGE_DISADVANTAGE_DIVISOR
        } else {
            DAMAGE_NORMAL_DIVISOR
        }
    }

    protected fun hasWeaponTriangleAdvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.character.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasAdvantageOver(targetSkill)
    }

    protected fun hasWeaponTriangleDisadvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.character.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasDisadvantageFrom(targetSkill)
    }

}
