package nl.t64.cot.components.cutscene

import nl.t64.cot.constants.ScreenType


class CutsceneContainer {

    private val cutscenesPlayedThisCycle: MutableMap<String, Boolean> = createCutsceneMap()
    private val cutsceneEverPlayedBefore: MutableMap<String, Boolean> = createCutsceneMap()

    fun updateOutdatedData() {
        updateOutdatedData(cutscenesPlayedThisCycle)
        updateOutdatedData(cutsceneEverPlayedBefore)
    }

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

    private fun updateOutdatedData(playedCutscenes: MutableMap<String, Boolean>) {
        val currentCutscenes: Map<String, Boolean> = createCutsceneMap()
        currentCutscenes
            .filterKeys { it !in playedCutscenes }
            .forEach { playedCutscenes[it.key] = it.value }
        playedCutscenes.keys.retainAll(currentCutscenes.keys)
    }

    private fun createCutsceneMap(): MutableMap<String, Boolean> {
        return ScreenType.entries
            .filter { it.isCutscene() }
            .associate { it.id to false }
            .toMutableMap()
    }

}
