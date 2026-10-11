package nl.t64.cot.screens.battle.scene

import nl.t64.cot.resources.ConfigDataLoader


object BattleSceneAnimationDatabase {

    private val animations: Map<String, BattleSceneAnimation> = ConfigDataLoader.createBattleSceneAnimations()

    fun getAnimation(animationId: BattleSceneAnimationId): BattleSceneAnimation {
        return animations[animationId.key]!!
    }

}
