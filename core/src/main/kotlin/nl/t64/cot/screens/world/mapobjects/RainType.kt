package nl.t64.cot.screens.world.mapobjects


/**
 * Hoe de regen op een map uitpakt. De enum-naam in kleine letters is de waarde van de map-property.
 */
enum class RainType(
    val isDimmingSunlight: Boolean,
    val isGreyingLight: Boolean
) {
    SUNNY(isDimmingSunlight = false, isGreyingLight = false),
    CLOUDY(isDimmingSunlight = true, isGreyingLight = false),
    GREY(isDimmingSunlight = true, isGreyingLight = true);

    companion object {
        fun of(id: String): RainType {
            return valueOf(id.trim().uppercase())
        }
    }
}
