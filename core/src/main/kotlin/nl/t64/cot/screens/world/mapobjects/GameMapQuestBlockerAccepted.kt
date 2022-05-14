package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import nl.t64.cot.components.quest.QuestState


class GameMapQuestBlockerAccepted(
    rectObject: RectangleMapObject,
    private val isActiveWhenQuestIsAccepted: Boolean
) : GameMapQuestBlocker(rectObject) {

    override fun update() {
        val isAccepted = quest!!.isCurrentStateEqualOrHigherThan(QuestState.ACCEPTED)
        checkBlocker(isAccepted == isActiveWhenQuestIsAccepted)
    }

}
