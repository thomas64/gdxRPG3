package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property


class GameMapQuestBlockerComplete(
    rectObject: RectangleMapObject,
    private val isActiveWhenTaskIsComplete: Boolean
) : GameMapQuestBlocker(rectObject) {

    private val taskId: String = rectObject.property("task")

    override fun update() {
        val isComplete = quest!!.isTaskComplete(taskId)
        checkBlocker(isComplete == isActiveWhenTaskIsComplete)
    }

}
