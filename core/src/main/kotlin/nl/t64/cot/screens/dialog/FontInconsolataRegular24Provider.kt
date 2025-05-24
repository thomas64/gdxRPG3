package nl.t64.cot.screens.dialog

import com.badlogic.gdx.graphics.g2d.BitmapFont
import nl.t64.cot.Utils.resourceManager

object FontInconsolataRegular24Provider {
    private const val FONT = "fonts/inconsolata_regular_24.ttf"
    private const val FONT_SIZE = 24
    private const val LINE_HEIGHT = 26f

    val font: BitmapFont by lazy {
        resourceManager.getTrueTypeAsset(FONT, FONT_SIZE).apply {
            data.setLineHeight(LINE_HEIGHT)
            data.markupEnabled = true
        }
    }
}
