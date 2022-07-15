package nl.t64.cot.screens.world.debug

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.screens.world.Camera
import nl.t64.cot.screens.world.entity.Entity


class DebugRenderer(
    private val camera: Camera
) {
    private val shapeRenderer = ShapeRenderer()
    private var showObjects = false

    fun dispose() {
        shapeRenderer.dispose()
    }

    fun setShowObjects() {
        if (preferenceManager.isInDebugMode) {
            showObjects = !showObjects
        }
    }

    fun possibleRenderObjects(entities: List<Entity>) {
        if (showObjects) {
            renderObjects(entities)
        }
    }

    private fun renderObjects(entities: List<Entity>) {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        entities.forEach { it.debug(shapeRenderer) }
        mapManager.currentMap.debug(shapeRenderer)

        shapeRenderer.end()
    }

}
