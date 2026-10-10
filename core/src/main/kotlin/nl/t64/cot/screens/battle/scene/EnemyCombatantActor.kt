package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.g2d.TextureRegion


class EnemyCombatantActor(
    enemyId: String,
    combatantScale: Float
) : CombatantActor(CombatantImages.getEnemyRegion(enemyId)) {

    init {
        setSize(region.regionWidth * combatantScale, region.regionHeight * combatantScale)
    }

    override fun getCurrentRegion(): TextureRegion {
        return region
    }

}
