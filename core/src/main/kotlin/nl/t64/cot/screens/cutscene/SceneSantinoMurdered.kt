package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.screens.world.entity.Direction


class SceneSantinoMurdered : CutsceneScreen() {

    private lateinit var santino: CutsceneActor
    private lateinit var santinoDead: Image
    private lateinit var garrin: CutsceneActor
    private lateinit var guard: CutsceneActor

    override fun prepare() {
        santino = CutsceneActor.createCharacter("santino")
        santinoDead = Utils.createImage("sprites/characters/damage3.png", 144, 240, 48, 48)
        garrin = CutsceneActor.createCharacter("man12")
        guard = CutsceneActor.createCharacter("soldier01")

        actorsStage.addActor(santino)
        actorsStage.addActor(santinoDead)
        actorsStage.addActor(garrin)
        actorsStage.addActor(guard)
        actions = listOf(test(), // todo
                         end())  // todo
    }

    private fun test(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("lastdenn")
                playBgm(AudioEvent.BGM_MURDER)
                setCameraPosition(768f, 1296f)
                santinoDead.isVisible = false
                santino.isVisible = true
                santino.setPosition(816f, 1272f)
                santino.direction = Direction.EAST
                garrin.isVisible = true
                garrin.setPosition(864f, 1272f)
                garrin.direction = Direction.WEST
                guard.isVisible = true
                guard.setPosition(816f, 1584f)
                guard.direction = Direction.SOUTH
            },

            actionFadeIn(),

            Actions.delay(1f),

            Actions.run { showConversationDialog("garrin_vs_santino_1", "mozes") }
        )
    }

    private fun end(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { exitScreen() }
        )
    }


    override fun exitScreen() {
        endCutsceneAndOpenMapAnd("lastdenn", "scene_santino_murdered") {
            gameData.clock.setTimeOfDay("14:00")
        }
    }

}
