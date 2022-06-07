package nl.t64.cot.subjects

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.screens.world.GameMap


interface MapObserver {

    fun onNotifyFadeOut(actionAfterFade: () -> Unit, transitionColor: Color, duration: Float)
    fun onNotifyMapChanged(currentMap: GameMap)
    fun onNotifyShakeCamera()
    fun onNotifyStartCutscene(cutsceneId: String)

}
