package nl.t64.cot.components.cutscene


class CutsceneContainer {

    private val cutscenes: MutableMap<String, Boolean> = mutableMapOf(
        Pair(CutsceneId.SCENE_INTRO, false)
    )

    fun isPlayed(cutsceneId: String): Boolean {
        return cutscenes[cutsceneId]!!
    }

    fun setPlayed(cutsceneId: String) {
        cutscenes[cutsceneId] = true
    }

}
