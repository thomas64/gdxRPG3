package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.AbilityItemId
import nl.t64.cot.components.party.skills.SkillItemId


class AttackData {
    var attacker: String = ""
    var target: String = ""
    var weaponSkill: SkillItemId? = null

    var hasAdvantage: Boolean = false
    var hasDisadvantage: Boolean = false
    var isCriticalHit: Boolean = false
    var damage: Int = 0

    var isStaggered: Boolean? = null

    var isMissed: Boolean = false
    var isBlocked: Boolean = false
    var targetShieldBrokeMessage: String? = null
    var attackerWeaponBrokeMessage: String? = null
    var attackerShieldBrokeMessage: String? = null
    var isTargetDead: Boolean = false

    var isHeal: Boolean = false
    var perform: AbilityItemId? = null
    var magicAbility: AbilityItemId? = null

    var castMessage: String = ""

    fun isMelee(): Boolean {
        return !isMagic() && !isRanged()
    }

    fun isMagic(): Boolean {
        return magicAbility != null
    }

    fun isRanged(): Boolean {
        return weaponSkill?.isRangedWeapon() == true
    }

    fun isSwung(): Boolean {
        return weaponSkill?.isSwungWeapon() == true
    }
}
