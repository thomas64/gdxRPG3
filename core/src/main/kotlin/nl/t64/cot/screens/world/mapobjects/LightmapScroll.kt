package nl.t64.cot.screens.world.mapobjects


/**
 * Hoe een lightmap over de map beweegt. Geldt alleen voor de lightmap_map, niet voor de lightmap_camera.
 */
enum class LightmapScroll {
    NONE,
    DIAGONAL,
    VERTICAL
}
