package nl.t64.cot.components.battle

class AttackData {
    var attacker: String = ""
    var target: String = ""

    var hasAdvantage: Boolean = false
    var hasDisadvantage: Boolean = false
    var isCriticalHit: Boolean = false
    var damage: Int = 0

    var isStaggered: Boolean? = null

    var isMissed: Boolean = false
    var isBlocked: Boolean = false
    var targetShieldBrokeMessage: String? = null
    var attackerWeaponBrokeMessage: String? = null
    var isTargetDead: Boolean = false

    var isHeal: Boolean = false

    var castMessage: String = ""
}
