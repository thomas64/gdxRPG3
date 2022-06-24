package nl.t64.cot.components.cutscene


class CutsceneContainer {

    private val cutscenes: MutableMap<String, Boolean> = mutableMapOf(
        Pair(CutsceneId.SCENE_INTRO, false),
        Pair(CutsceneId.SCENE_ARDOR_1, false),
        Pair(CutsceneId.SCENE_ARDOR_2, false),
        Pair(CutsceneId.SCENE_END_OF_CYCLE_2, false)
    )

    fun isRepeatable(cutsceneId: String): Boolean {
        return cutsceneId in listOf(CutsceneId.SCENE_ARDOR_2)
    }

    fun isPlayed(cutsceneId: String): Boolean {
        return cutscenes[cutsceneId]!!
    }

    fun setPlayed(cutsceneId: String) {
        cutscenes[cutsceneId] = true
    }

}
