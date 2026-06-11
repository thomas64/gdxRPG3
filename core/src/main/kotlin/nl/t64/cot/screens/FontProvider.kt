package nl.t64.cot.screens

import com.badlogic.gdx.graphics.g2d.BitmapFont
import nl.t64.cot.Utils

object FontProvider {

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

}
