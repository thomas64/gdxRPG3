package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch


/**
 * Hoe een lightmap met de al getekende wereld wordt gemengd.
 *
 * [ADDITIVE] telt de lightmap bij de wereld op, dus alleen lichter. Zwart is neutraal.
 * [MULTIPLY] vermenigvuldigt de wereld met de lightmap, dus alleen donkerder. Wit is neutraal,
 * en een gekleurd grijs kleurt de scene mee (blauwig grijs geeft een nachttint).
 * [INVERSE_ALPHA] geeft (wereld + lightmap) × (1 - alpha van de lightmap), dus additief waar de
 * lightmap doorzichtig is en richting zwart waar hij ondoorzichtig is.
 */
enum class LightmapBlend(
    private val sourceFactor: Int,
    private val destinationFactor: Int
) {
    ADDITIVE(GL20.GL_ONE, GL20.GL_ONE),
    MULTIPLY(GL20.GL_ZERO, GL20.GL_SRC_COLOR),
    INVERSE_ALPHA(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    fun applyTo(batch: Batch) {
        batch.setBlendFunction(sourceFactor, destinationFactor)
    }
}
