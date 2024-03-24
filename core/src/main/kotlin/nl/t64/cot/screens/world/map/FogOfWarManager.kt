package nl.t64.cot.screens.world.map

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.ProfileManager
import nl.t64.cot.screens.world.Camera
import nl.t64.cot.subjects.ProfileObserver


class FogOfWarManager : ProfileObserver {

    private lateinit var fogOfWar: FogOfWar
    private lateinit var currentMap: GameMap

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        fogOfWar = FogOfWar()
        onNotifySaveProfile(profileManager)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("fogOfWar", fogOfWar)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        fogOfWar = profileManager.getProperty("fogOfWar") ?: FogOfWar()
    }

    fun setNewMap(newMap: GameMap, camera: Camera) {
        currentMap = newMap
        if (camera.isZoomPossible()) {
            fogOfWar.putIfAbsent(currentMap)
        }
    }

    fun update(playerPosition: Vector2, dt: Float) {
        fogOfWar.update(playerPosition, currentMap, dt)
    }

    fun draw(shapeRenderer: ShapeRenderer) {
        fogOfWar.draw(shapeRenderer, currentMap.mapTitle)
    }

}
