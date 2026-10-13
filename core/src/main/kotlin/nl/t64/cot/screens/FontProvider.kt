package nl.t64.cot.screens

import com.badlogic.gdx.graphics.g2d.BitmapFont
import nl.t64.cot.Utils


object FontProvider {

    private const val OUTLINE_WIDTH = 1.5f

    val default: BitmapFont by lazy {
        BitmapFont().apply { data.markupEnabled = true }
    }

    val inconsolata24: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/inconsolata_regular_24.ttf", 24).apply {
//            data.setLineHeight(26f)
            data.markupEnabled = true
        }
    }

    val barlow45: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/barlow_regular_45.ttf", 45)
            .apply { data.markupEnabled = true }
    }

    // with a thin black border, for the numbers in the battle scenes.
    val barlowOutlined24: BitmapFont by lazy { createBarlowOutlined(24) }
    val barlowOutlined48: BitmapFont by lazy { createBarlowOutlined(48) }
    val barlowOutlined54: BitmapFont by lazy { createBarlowOutlined(54) }
    val barlowOutlined60: BitmapFont by lazy { createBarlowOutlined(60) }
    val barlowOutlined66: BitmapFont by lazy { createBarlowOutlined(66) }

    val calibriLight28: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/calibri_light_28.ttf", 28)
            .apply { data.markupEnabled = true }
    }

    val spectralExtraBold20: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/spectral_extra_bold_20.ttf", 20)
            .apply { data.markupEnabled = true }
    }

    val spectralExtraBold28: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/spectral_extra_bold_28.ttf", 28)
            .apply { data.markupEnabled = true }
    }

    val spectralRegular24: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/spectral_regular_24.ttf", 24)
            .apply { data.markupEnabled = true }
    }

    val fffTusjBold200: BitmapFont by lazy {
        Utils.resourceManager.getTrueTypeAsset("fonts/fff_tusj_bold_30.ttf", 200)
            .apply { data.markupEnabled = true }
    }

    // every size needs its own copy of the ttf, because the asset manager keeps one font per file.
    private fun createBarlowOutlined(fontSize: Int): BitmapFont {
        return Utils.resourceManager.getTrueTypeAsset("fonts/barlow_regular_$fontSize.ttf", fontSize, OUTLINE_WIDTH)
    }

}
