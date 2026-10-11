package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import nl.t64.cot.screens.FontProvider


enum class NumberStyle(val label: String?, val color: Color) {

    MISS_OR_BLOCK(null, Color.LIGHT_GRAY),
    NORMAL(null, Color.WHITE),
    ADVANTAGE("Advantage", Color.ORANGE),
    DISADVANTAGE("Disadvantage", Color.SKY),
    CRITICAL("Critical!", Color.RED);

    fun getFont(): BitmapFont {
        return when (this) {
            MISS_OR_BLOCK, NORMAL -> FontProvider.barlowOutlined68
            ADVANTAGE -> FontProvider.barlowOutlined76
            DISADVANTAGE -> FontProvider.barlowOutlined60
            CRITICAL -> FontProvider.barlowOutlined88
        }
    }

}
