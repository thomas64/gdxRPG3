package nl.t64.cot.screens.battle.scene

import nl.t64.cot.resources.ConfigDataLoader


object AttackAnimationDatabase {

    private val animations: Map<String, AttackAnimation> = ConfigDataLoader.createAttackAnimations()

    fun getAnimation(animationId: AttackAnimationId): AttackAnimation {
        return animations[animationId.key]!!
    }

}
