package nl.t64.cot.screens.world.debug

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.Camera


class GridRenderer(
    private val camera: Camera
) {
    private val shapeRenderer = ShapeRenderer()
    private var mapPixelWidth: Float = 0f
    private var mapPixelHeight: Float = 0f
    private var showGrid = false

    fun dispose() {
        shapeRenderer.dispose()
    }

    fun setShowGrid() {
        if (preferenceManager.isInDebugMode) {
            showGrid = !showGrid
        }
    }

    fun possibleRender() {
        if (showGrid) {
            render()
        }
    }

    private fun render() {
        startShapeRenderer()
        setMapSizes()
        setHorizontalLines()
        setVerticalLines()
        shapeRenderer.end()
    }

    private fun startShapeRenderer() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    }

    private fun setMapSizes() {
        mapPixelWidth = mapManager.currentMap.pixelWidth
        mapPixelHeight = mapManager.currentMap.pixelHeight
    }

    private fun setHorizontalLines() {
        var x = 0f
        while (x < mapPixelWidth) {
            shapeRenderer.line(x, 0f, x, mapPixelHeight)
            x += Constant.TILE_SIZE
        }
    }

    private fun setVerticalLines() {
        var y = 0f
        while (y < mapPixelHeight) {
            shapeRenderer.line(0f, y, mapPixelWidth, y)
            y += Constant.TILE_SIZE
        }
    }

}
