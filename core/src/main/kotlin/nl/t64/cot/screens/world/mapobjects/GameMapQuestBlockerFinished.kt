package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import nl.t64.cot.components.quest.QuestState


class GameMapQuestBlockerFinished(
    rectObject: RectangleMapObject,
    private val isActiveWhenQuestIsFinished: Boolean
) : GameMapQuestBlocker(rectObject) {

    override fun update() {
        val isFinished = quest.currentState == QuestState.FINISHED
        checkBlocker(isFinished == isActiveWhenQuestIsFinished)
    }

}
