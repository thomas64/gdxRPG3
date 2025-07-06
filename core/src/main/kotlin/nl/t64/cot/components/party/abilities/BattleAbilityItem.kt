package nl.t64.cot.components.party.abilities

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
    private val id: AbilityItemId = abilityItem.id
    val name: String = abilityItem.name
    val ap: Int = abilityItem.ap
    val sp: Int = abilityItem.sp

    protected val currentWeapon: InventoryItem? get() = attacker.character.getInventoryItem(InventoryGroup.WEAPON)

    lateinit var target: Participant

    override fun toString(): String {
        val apField = if (name != "Back") String.format("%3d AP", ap) else ""
        val spField = if (sp > 0) " | $sp SP" else "       "
        val totalWidth = 28
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
        singleAttack.attacker = this.attacker.character.name
        singleAttack.target = this.target.character.name

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

    fun isWeaponAllowed(): Boolean {
        return abilityItem.isWeaponAllowed(currentWeapon)
    }

    protected fun calculateHitPercentageCapped(): Int {
        return calculateHitPercentage().coerceAtMost(100)
    }

    fun calculateHitPercentage(): Int {
        val attackerHitPercentage: Int = (attacker.character.getCalculatedTotalHit() * abilityItem.hitMultiplier).roundToInt()
        val weaponTriangle: Int = getAdvantageBonusHit()
        return (attackerHitPercentage + weaponTriangle).coerceAtLeast(0)
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
        val attack: Float = attacker.character.getCalculatedTotalDamage() * abilityItem.damageMultiplier
        val protection: Int = target.character.getCalculatedTotalProtection()
        val damage: Float = attack - protection
        val disadvantagePenalty: Float = getAdvantageBonusDamage()
        return (damage / disadvantagePenalty).roundToInt().coerceAtLeast(1)
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
        if (name == "Back") return ""
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
