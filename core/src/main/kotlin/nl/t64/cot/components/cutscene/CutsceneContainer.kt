package nl.t64.cot.components.cutscene


class CutsceneContainer {

    private val cutscenes: MutableMap<String, Boolean> = mutableMapOf(
        CutsceneId.SCENE_INTRO to false,
        CutsceneId.SCENE_ARDOR_1 to false,
        CutsceneId.SCENE_ARDOR_2 to false,
        CutsceneId.SCENE_END_OF_CYCLE_2 to false
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
