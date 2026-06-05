package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.areAllTrue
import nl.t64.cot.components.cutscene.CutsceneContainer
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapCutscene(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), CollisionObserver {

    private val cutsceneScreenType: ScreenType = ScreenType.fromId(rectObject.name)
    private val conditions: List<String> = createConditions(rectObject)
    private val mustContinueBgm: Boolean = rectObject.propertyOrNull<Boolean>("mustContinueBgm") ?: false

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle) && conditions.areAllTrue()) {
            possibleStartCutscene()
        }
    }

    private fun possibleStartCutscene() {
        val cutscenes: CutsceneContainer = gameData.cutscenes
        if (!cutscenes.isPlayedThisCycle(cutsceneScreenType)) {
            cutscenes.setPlayedThisCycle(cutsceneScreenType)
            if (mustContinueBgm) {
                worldScreen.startCutsceneWithoutBgmFading(cutsceneScreenType)
            } else {
                worldScreen.startCutscene(cutsceneScreenType)
            }
        }
    }

}
