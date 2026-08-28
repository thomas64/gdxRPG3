package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Texture
import nl.t64.cot.Utils.resourceManager


private const val LIGHTMAP_PATH = "sprites/lightmaps/%s.png"

/**
 * Elke png in sprites/lightmaps, met de manier waarop hij getekend moet worden.
 * De enum-naam in kleine letters is de bestandsnaam.
 *
 * [MOUNTAINS] is de uitzondering: die wordt als parallax-achtergrond in de gewone alpha-pass getekend,
 * vóór alle lightmaps, en gebruikt dus alleen de texture en niet de [blend].
 */
enum class Lightmap(
    val blend: LightmapBlend = LightmapBlend.ADDITIVE,
    val scroll: LightmapScroll = LightmapScroll.NONE
) {
    BUBBLES(scroll = LightmapScroll.VERTICAL),
    DARK_FOREST(blend = LightmapBlend.MULTIPLY),
    FOG(LightmapBlend.INVERSE_ALPHA, LightmapScroll.VERTICAL),
    FOREST(scroll = LightmapScroll.DIAGONAL),
    HEAVEN,
    LIGHT_OBJECT,
    LIGHT_PLAYER,
    MOUNTAINS,
    SUNLIGHT;

    val texture: Texture get() = resourceManager.getTextureAsset(String.format(LIGHTMAP_PATH, name.lowercase()))

    companion object {
        fun of(id: String): Lightmap {
            return valueOf(id.trim().uppercase())
        }

        fun ofAll(ids: String): List<Lightmap> {
            return ids.split(",").map { of(it) }
        }
    }
}
