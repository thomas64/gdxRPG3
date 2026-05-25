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
private const val CRIT_DAMAGE_MULTIPLIER = 1.5f

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

    protected open fun isBlock(): Boolean {
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
        val critSkill: SkillItemId = getCriticalHitSkillId()
        val attackerCriticalHitPercentage: Int = attacker.character.getCalculatedTotalSkillOf(critSkill) * 4
        val weaponTriangle: Int = getAdvantageBonusCrit()
        return (attackerCriticalHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    protected open fun getCriticalHitSkillId(): SkillItemId {
        return SkillItemId.WARRIOR
    }

    protected fun calculateCriticalDamageMinusProtection(): Int {
        return calculateCriticalDamage().minusProtection()
    }

    private fun calculateCriticalDamage(): Float {
        return calculateDamage() * CRIT_DAMAGE_MULTIPLIER
    }

    protected fun calculateCriticalDamageForVisual(): Float {
        return calculateDamageForVisual() * CRIT_DAMAGE_MULTIPLIER
    }

    fun calculateDamageMinusProtection(): Int {
        return calculateDamage().minusProtection()
    }

    private fun calculateDamage(): Float {
        val baseDamage: Float = attacker.character.getCalculatedTotalDamage() * abilityItem.damageMultiplier
        val damageWithGambler: Float = attacker.character.applyGamblerBonusTo(baseDamage)
        return damageWithGambler / getDisadvantageDamageDivisor()
    }

    protected fun calculateDamageForVisual(): Float {
        val baseDamage: Float = attacker.character.getCalculatedTotalDamage() * abilityItem.damageMultiplier
        return baseDamage / getDisadvantageDamageDivisor()
    }

    protected fun Float.minusProtection(): Int {
        val reductionPercentage: Float = getDamageReductionPercentage()
        val modifiedDamage: Float = this * (1f - (reductionPercentage / 100f))
        return modifiedDamage.roundToInt().coerceAtLeast(1)
    }

    protected open fun getDamageReductionPercentage(): Float {
        return target.character.getCalculatedTotalProtection().toFloat()
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

    protected fun getMultiplierForVisual(multiplier: Float): String {
        val formatted: String = when {
            multiplier % 1 == 0f -> String.format("%.1f", multiplier)       // hele getallen -> 1 decimaal
            multiplier * 10 % 1 == 0f -> String.format("%.1f", multiplier)  // één decimaal  -> 1 decimaal
            else -> String.format("%.3f", multiplier)                       // rest          -> 3 decimalen
        }
        val parts = formatted.split(".")
        return "${String.format("%3s", "x " + parts[0])}.${parts.getOrNull(1) ?: "0"}"
    }

    protected fun getGamblerBonusForVisual(): Int {
        return attacker.character.getCalculatedTotalSkillOf(SkillItemId.GAMBLER)
            .takeIf { it > 0 }
            ?.let { it * 5 }
            ?: 0
    }

    protected fun getDisadvantagePenaltyDamageForVisual(): String {
        return if (hasWeaponTriangleDisadvantage()) "x 0.666" else "x 1.0"
    }

    private fun possibleGetGrayPrefix(): String {
        if (abilityItem.isPreview) return ""
        return if (!isWeaponAllowed() || !hasEnoughApSp()) "[GRAY]" else ""
    }

    protected fun getAdvantageBonusHit(): Int {
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

    private fun getDisadvantageDamageDivisor(): Float {
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
