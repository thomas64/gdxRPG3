package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant


private const val TEXT_FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24

class BattleFieldTableBuilder {

    fun createBattleFieldTable(battleField: BattleField, currentParticipant: Participant): Table {
        val tableSkin: Skin = createSkin()

        val enemyNumberTable: Table = createEnemyNumberTable(battleField, tableSkin)
        val enemyTable: Table = if (currentParticipant.isHero) {
            createTargetFieldTable(battleField, currentParticipant, tableSkin)
        } else {
            createParticipantTable(battleField.enemySpaces, tableSkin)
        }
        val heroTable: Table = if (currentParticipant.isHero) {
            createHeroFieldTable(battleField, currentParticipant, tableSkin)
        } else {
            createParticipantTable(battleField.heroSpaces, tableSkin)
        }

        return Table(tableSkin).apply {
            bottom().left()
            setPosition(350f, 20f)
            add(enemyNumberTable).padLeft(60f)
            row()
            add(enemyTable).padLeft(60f)
            row()
            add(heroTable).padTop(4f)
        }
    }

    private fun createEnemyNumberTable(battleField: BattleField, skin: Skin): Table {
        val enemyCountMap: Map<String, Int> = battleField.createEnemyCountMap()
        val labelStyle: LabelStyle = createLabelStyle()
        return Table(skin).apply {
            defaults().width(60f).height(30f).center()
            battleField.enemySpaces.forEach {

                if (it != null && !it.character.isAlive) {
                    add().padRight(1f)
                } else if (it != null && enemyCountMap[it.character.id]!! > 1) {
                    add(Container(Label(it.character.name.last().toString(), labelStyle))).padRight(1f)
                } else {
                    add().padRight(1f)
                }
            }
        }
    }

    private fun createParticipantTable(spaces: MutableList<Participant?>, skin: Skin): Table {
        return Table(skin).apply {
            defaults().width(60f).height(60f).center()
            spaces.forEach {
                when (it) {
                    null -> addWhiteCell()
                    else -> addWhiteParticipantCell(it)
                }
            }
        }
    }

    private fun createTargetFieldTable(battleField: BattleField, currentParticipant: Participant, skin: Skin): Table {
        val ranges: List<Int> = battleField.getRangeOfHero(currentParticipant)

        return Table(skin).apply {
            defaults().width(60f).height(60f).center()
            battleField.enemySpaces.forEachIndexed { index, enemyAtSpace ->

                when {
                    enemyAtSpace == null && index in ranges -> addRedCell()
                    enemyAtSpace == null -> addWhiteCell()
                    index in ranges -> addRedParticipantCell(enemyAtSpace)
                    else -> addWhiteParticipantCell(enemyAtSpace)
                }
            }
        }
    }

    private fun createHeroFieldTable(battleField: BattleField, currentParticipant: Participant, skin: Skin): Table {
        val startingSpace = battleField.startingSpace
        val currentSpace = battleField.getCurrentSpace(currentParticipant)
        val actionPoints = currentParticipant.currentAP

        return Table(skin).apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->
                addHeroFieldCell(index, heroAtSpace, currentParticipant, startingSpace, currentSpace, actionPoints)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun Table.addHeroFieldCell(
        index: Int,
        heroAtSpace: Participant?,
        currentParticipant: Participant,
        startingSpace: Int,
        currentSpace: Int,
        actionPoints: Int,
    ) {
        val isInRange = index in startingSpace - actionPoints..startingSpace + actionPoints
        val isStartingSpace = index == startingSpace
        val isCurrentSpace = index == currentSpace

        when {
            startingSpace == -1 && heroAtSpace == currentParticipant -> addGoldParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace == null -> addWhiteCell()

            isStartingSpace && !isCurrentSpace -> addGoldCell()
            isInRange && heroAtSpace == null -> addGreenCell()
            isStartingSpace && isCurrentSpace -> addGoldParticipantCell(heroAtSpace!!)
            !isStartingSpace && isCurrentSpace -> addGreenParticipantCell(heroAtSpace!!)
            heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            else -> addWhiteCell()
        }
    }

    private fun Table.addWhiteParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()))
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addGoldParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE })
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addGreenParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE })
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addRedParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED })
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addWhiteCell() {
        add(Image(Utils.createFullBorderWhite())).padRight(1f)
    }

    private fun Table.addGoldCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE }).padRight(1f)
    }

    private fun Table.addGreenCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE }).padRight(1f)
    }

    private fun Table.addRedCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED }).padRight(1f)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTableMove(): Table {
        return createSelectionTable().apply {
            add("Move left or right and confirm.")
        }
    }

    private fun createImageOf(participant: Participant): Container<Image> {
        return Container(
            Image(Utils.getCharImage(participant.character.id)[0][1])
                .apply { setScaling(Scaling.none) }
        ).left()
    }

    private fun createSelectionTable(): Table {
        return Table(createSkin()).apply {
            columnDefaults(0).width(300f)
            top().left()
            setPosition(1050f, Gdx.graphics.height - 20f)
        }
    }

    private fun createLabelStyle(): LabelStyle {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return LabelStyle(font, Color.WHITE)
    }

    private fun createSkin(): Skin {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return Skin().apply { add("default", LabelStyle(font, Color.WHITE)) }
    }

}
