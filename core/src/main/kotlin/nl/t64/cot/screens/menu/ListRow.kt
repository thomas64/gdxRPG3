package nl.t64.cot.screens.menu

import com.badlogic.gdx.utils.Array
import ktx.collections.toGdxArray


/**
 * A row for a scene2d List, which shows the toString() of its items.
 * The List selects its items by equals, so two rows with the same text would be selected together,
 * and the second row could never be selected on its own.
 * This class keeps the identity equals of Any, which makes every row unique, even with the same text.
 */
class ListRow(val text: String) {
    override fun toString(): String = text
}

fun Array<String>.toListRows(): Array<ListRow> {
    return this.map { ListRow(it) }.toGdxArray()
}
