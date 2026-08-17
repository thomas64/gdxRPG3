package nl.t64.cot.components.blocker

import com.badlogic.gdx.math.Rectangle


class RemovedBlockerContainer {

    private val removedBlockers: MutableList<RectangleWithId> = mutableListOf()

    fun contains(mapTitle: String, rectangle: Rectangle): Boolean {
        return removedBlockers.contains(RectangleWithId(mapTitle, rectangle))
    }

    fun add(mapTitle: String, rectangle: Rectangle) {
        removedBlockers.add(RectangleWithId(mapTitle, rectangle))
    }

}
