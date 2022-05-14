package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property


class GameMapQuestBlockerActive(
    rectObject: RectangleMapObject,
    private val isActiveWhenTaskIsActive: Boolean
) : GameMapQuestBlocker(rectObject) {

    private val taskId: String = rectObject.property("task")

    override fun update() {
        if (isActiveWhenTaskIsActive) {
            checkBlocker(quest!!.isTaskActive(taskId))
        }
    }

}
