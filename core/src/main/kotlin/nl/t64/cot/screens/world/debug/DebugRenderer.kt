package nl.t64.cot.screens.world.debug

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.Camera
import nl.t64.cot.screens.world.entity.Entity


class DebugRenderer(
    private val camera: Camera,
    private val player: Entity,
) {
    private lateinit var entities: List<Entity>

    private val shapeRenderer = ShapeRenderer()
    private var showObjects = false

    fun dispose() {
        shapeRenderer.dispose()
    }

    fun setShowObjects() {
        if (Utils.preferenceManager.isInDebugMode) {
            showObjects = !showObjects
        }
    }

    fun possibleRenderObjects(vararg newEntities: List<Entity>) {
        if (showObjects) {
            entities = newEntities.toList().flatten()
            renderObjects()
        }
    }

    private fun renderObjects() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        player.debug(shapeRenderer)
        entities.forEach { it.debug(shapeRenderer) }
        mapManager.currentMap.debug(shapeRenderer)

        shapeRenderer.end()
    }

}
