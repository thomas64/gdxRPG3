package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapCutscene(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), CollisionObserver {

    private val cutsceneId: String = rectObject.name
    private val conditionIds: List<String> = createConditions(rectObject)
    private val mustContinueBgm: Boolean = rectObject.propertyOrNull<Boolean>("mustContinueBgm") ?: false
    private var hasStartedTemp: Boolean = false

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle) && ConditionDatabase.isMeetingConditions(conditionIds)) {
            possibleStartCutscene()
        }
    }

    private fun possibleStartCutscene() {
        val cutscenes = gameData.cutscenes
        if (!cutscenes.isPlayed(cutsceneId) || (cutscenes.isRepeatable(cutsceneId) && !hasStartedTemp)) {
            cutscenes.setPlayed(cutsceneId)
            hasStartedTemp = true
            val cutsceneType = ScreenType.valueOf(cutsceneId.uppercase())
            if (mustContinueBgm){
                worldScreen.startCutsceneWithoutBgmFading(cutsceneType)
            } else {
                worldScreen.startCutscene(cutsceneType)
            }
        }
    }

}
