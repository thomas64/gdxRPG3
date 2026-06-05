package nl.t64.cot.components.cutscene

import nl.t64.cot.constants.ScreenType


class CutsceneContainer {

    private val cutscenes: Map<String, Boolean> = ScreenType.entries
        .filter { it.isCutscene() }
        .associate { it.id to false }

    private val cutscenesPlayedThisCycle: MutableMap<String, Boolean> = cutscenes.toMutableMap()
    private val cutsceneEverPlayedBefore: MutableMap<String, Boolean> = cutscenes.toMutableMap()

    fun isPlayedThisCycle(cutsceneType: ScreenType): Boolean {
        return cutscenesPlayedThisCycle[cutsceneType.id]!!
    }

    fun isEverPlayedBefore(cutsceneType: ScreenType): Boolean {
        return cutsceneEverPlayedBefore[cutsceneType.id]!!
    }

    fun setPlayedThisCycle(cutsceneType: ScreenType) {
        cutscenesPlayedThisCycle[cutsceneType.id] = true
    }

    fun setPlayedEver(cutsceneType: ScreenType) {
        cutsceneEverPlayedBefore[cutsceneType.id] = true
    }

    fun reset() {
        cutscenesPlayedThisCycle.forEach { cutscenesPlayedThisCycle[it.key] = false }
    }

}
