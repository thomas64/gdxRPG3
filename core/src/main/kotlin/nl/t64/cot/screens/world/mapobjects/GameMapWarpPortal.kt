package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.ActionObserver


class GameMapWarpPortal(rectObject: RectangleMapObject, fromMapName: String) : GameMapObject(
    rectObject.rectangle,
), ActionObserver {

    private val fromMapName: String = fromMapName.uppercase()

    init {
        brokerManager.actionObservers.addObserver(this)
    }

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        if (checkRect.overlaps(rectangle)) {
            activateOrUse()
        }
    }

    private fun activateOrUse() {
        if (gameData.portals.isActivated(fromMapName)) {
            brokerManager.componentObservers.notifyShowWarpScreen(fromMapName)
        } else {
            brokerManager.messageObservers.notifyShowMessageTooltip("Portal activated.")
            playSe(AudioEvent.SE_ACTIVATE)
            gameData.portals.activate(fromMapName)
        }
    }

}
