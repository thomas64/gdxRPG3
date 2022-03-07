package nl.t64.cot.components.cutscene

import nl.t64.cot.GameTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class CutsceneTest : GameTest() {

    private lateinit var cutsceneContainer: CutsceneContainer

    @BeforeEach
    private fun beforeEach() {
        cutsceneContainer = CutsceneContainer()
    }

    @Test
    fun `When cutscene has been played, should set boolean in container to true`() {
        assertThat(cutsceneContainer.isPlayed(CutsceneId.SCENE_INTRO)).isFalse
        cutsceneContainer.setPlayed(CutsceneId.SCENE_INTRO)
        assertThat(cutsceneContainer.isPlayed(CutsceneId.SCENE_INTRO)).isTrue
    }

}
