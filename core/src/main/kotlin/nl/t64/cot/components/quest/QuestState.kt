package nl.t64.cot.components.quest


enum class QuestState(val index: Int) {
    UNKNOWN(0),
    KNOWN(1),
    ACCEPTED(2),
    UNCLAIMED(3),
    FINISHED(4);

    fun isEqualOrHigherThan(otherState: QuestState): Boolean = index >= otherState.index
    fun isEqualOrLowerThan(otherState: QuestState): Boolean = index <= otherState.index

}
