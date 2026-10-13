package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import nl.t64.cot.screens.FontProvider


enum class NumberStyle(val label: String?, val color: Color) {

    MISS_OR_BLOCK(null, Color.LIGHT_GRAY),
    NORMAL(null, Color.WHITE),
    ADVANTAGE("Effective", Color.ORANGE),
    DISADVANTAGE("Resisted", Color.SKY),
    CRITICAL("Critical!", Color.RED);

    fun getFont(): BitmapFont {
        return when (this) {
            MISS_OR_BLOCK, NORMAL -> FontProvider.barlowOutlined54
            ADVANTAGE -> FontProvider.barlowOutlined60
            DISADVANTAGE -> FontProvider.barlowOutlined48
            CRITICAL -> FontProvider.barlowOutlined66
        }
    }

}
