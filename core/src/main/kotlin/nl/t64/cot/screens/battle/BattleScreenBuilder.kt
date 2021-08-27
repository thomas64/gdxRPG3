package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.battle.EnemyItem
import nl.t64.cot.constants.Constant


private const val TEXT_FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
private const val TITLE_TEXT = "Battle...!"

object BattleScreenBuilder {

    fun createBattleTitle(): Label {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        val style = Label.LabelStyle(font, Color.WHITE)
        return Label(TITLE_TEXT, style).apply {
            setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
            isVisible = false
        }
    }

    fun createHeroTable(): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(0).width(30f)
            columnDefaults(1).width(Constant.FACE_SIZE)
            columnDefaults(2).width(200f)
            top().left()
            setPosition(50f, Gdx.graphics.height.toFloat() - 50f)
            isVisible = false
            gameData.party.getAllHeroes().forEachIndexed { i, hero ->
                add((i + 1).toString())
                add(Utils.getFaceImage(hero.id).apply { if (!hero.isAlive) color = Color.DARK_GRAY })
                add(hero.name).padLeft(20f).row()
            }
        }
    }

    fun createButtonTable(): Table {
        return Table(createSkin()).apply {
            defaults().height(50f)
            top()
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height.toFloat() - 50f)
            isVisible = false
            add().row()
            add("This battle screen is a temporary placeholder for the real turn based").row()
            add("strategic battle gameplay that will replace this screen in the future.").row()
            add().row()
            add("(W)in battle").row()
            add("(F)lee battle").row()
            add("Kill hero (1-6)")
        }
    }

    fun createEnemyTable(enemies: List<EnemyItem>): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(0).width(Constant.FACE_SIZE)
            columnDefaults(1).width(200f)
            top().left()
            setPosition(Gdx.graphics.width.toFloat() - Constant.FACE_SIZE - 200f - 50f,
                        Gdx.graphics.height.toFloat() - 50f)
            isVisible = false
            enemies.forEach {
                add(Utils.getFaceImage(it.id))
                add(it.name).padLeft(20f).row()
            }
        }
    }

    private fun createSkin(): Skin {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return Skin().apply {
            add("default", Label.LabelStyle(font, Color.WHITE))
        }
    }

}