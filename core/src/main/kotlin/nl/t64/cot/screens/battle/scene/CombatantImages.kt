package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.resources.SpriteConfig


private const val COMBATANT_PATH = "sprites/combatants/"
// an enemy sheet has the same layout as the walking sprite sheet of that enemy: 4 x 2 color variants.
private const val ENEMY_SHEET_COLS = 4
private const val ENEMY_SHEET_ROWS = 2

object CombatantImages {

    fun getHeroRegion(heroId: String): TextureRegion {
        return TextureRegion(loadTexture(heroId))
    }

    fun getEnemyRegion(enemyId: String): TextureRegion {
        val config: SpriteConfig = resourceManager.getSpriteConfig(enemyId)!!
        val sheet: Texture = loadTexture(config.source)
        val cellWidth: Int = sheet.width / ENEMY_SHEET_COLS
        val cellHeight: Int = sheet.height / ENEMY_SHEET_ROWS
        return TextureRegion(sheet, (config.col - 1) * cellWidth, (config.row - 1) * cellHeight, cellWidth, cellHeight)
            .apply { insetHalfTexel(sheet) }
    }

    // when scaled, nearest sampling at the edge can hit the first texel of the neighboring cell.
    private fun TextureRegion.insetHalfTexel(sheet: Texture) {
        val halfTexelU: Float = 0.5f / sheet.width
        val halfTexelV: Float = 0.5f / sheet.height
        this.setRegion(this.u + halfTexelU, this.v + halfTexelV, this.u2 - halfTexelU, this.v2 - halfTexelV)
    }

    private fun loadTexture(imageName: String): Texture {
        return resourceManager.getTextureAsset("$COMBATANT_PATH$imageName.png")
            .apply { setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest) }
    }

}
