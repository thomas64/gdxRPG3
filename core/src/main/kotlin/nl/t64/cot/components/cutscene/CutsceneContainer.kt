package nl.t64.cot.components.cutscene


class CutsceneContainer {

    private val cutscenes: MutableMap<String, Boolean> = mutableMapOf(
        CutsceneId.SCENE_INTRO to false,
        CutsceneId.SCENE_ARDOR_FIRST_TIME to false,
        CutsceneId.SCENE_HONEYWOOD_FARM_ATTACK_1 to false,
        CutsceneId.SCENE_HONEYWOOD_FARM_ATTACK_1_ALT to false,
        CutsceneId.SCENE_HONEYWOOD_FARM_TOO_LATE to false,
        CutsceneId.SCENE_SANTINO_MURDERED to false,
        CutsceneId.SCENE_ARDOR_LATER_TIME to false
    )

    fun isPlayed(cutsceneId: String): Boolean {
        return cutscenes[cutsceneId]!!
    }

    fun setPlayed(cutsceneId: String) {
        cutscenes[cutsceneId] = true
    }

    fun reset() {
        cutscenes
            .filter { isResettable(it.key) }
            .forEach { cutscenes[it.key] = false }
    }

    private fun isResettable(cutsceneId: String): Boolean {
        return cutsceneId in listOf(CutsceneId.SCENE_HONEYWOOD_FARM_ATTACK_1_ALT,
                                    CutsceneId.SCENE_HONEYWOOD_FARM_TOO_LATE,
                                    CutsceneId.SCENE_SANTINO_MURDERED,
                                    CutsceneId.SCENE_ARDOR_LATER_TIME)
    }

}
