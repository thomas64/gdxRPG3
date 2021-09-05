package nl.t64.cot.subjects

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.screens.world.GameMap


class MapSubject {

    private val observers: MutableList<MapObserver> = ArrayList()

    fun addObserver(observer: MapObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: MapObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyFadeOut(actionAfterFade: () -> Unit, transitionColor: Color = Color.BLACK) {
        observers.forEach { it.onNotifyFadeOut(actionAfterFade, transitionColor) }
    }

    fun notifyMapChanged(currentMap: GameMap) {
        observers.forEach { it.onNotifyMapChanged(currentMap) }
    }

    fun notifyShakeCamera() {
        observers.forEach { it.onNotifyShakeCamera() }
    }

    fun notifyStartCutscene(cutsceneId: String) {
        observers.forEach { it.onNotifyStartCutscene(cutsceneId) }
    }

}
